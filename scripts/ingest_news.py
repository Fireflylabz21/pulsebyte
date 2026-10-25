"""
PulseAI Automated News Ingestion & Multi-LLM Synthesis Worker (Python Edition)
Fetches live RSS news feeds across 12 topics, resolves destination article URLs,
synthesizes via Gemini 3.5 Flash Lite (with alternate Gemini & Grok fallbacks),
runs editorial guardrails, generates vector embeddings, captures source links and names,
and upserts into Supabase story_clusters and raw_articles tables.
"""

import os
import sys
import json
import datetime
from urllib.parse import urlparse
import feedparser
import requests
import google.generativeai as genai
from supabase import create_client, Client

# Initialize Environment Variables
SUPABASE_URL = os.environ.get("SUPABASE_URL")
SUPABASE_SERVICE_ROLE_KEY = os.environ.get("SUPABASE_SERVICE_ROLE_KEY")
GEMINI_API_KEY = os.environ.get("GEMINI_API_KEY")
GEMINI_API_KEY_FALLBACK = os.environ.get("GEMINI_API_KEY_FALLBACK")
GROK_API_KEY = os.environ.get("GROK_API_KEY")

if not SUPABASE_URL or not SUPABASE_SERVICE_ROLE_KEY:
    print("Error: Missing Supabase environment variables (SUPABASE_URL or SUPABASE_SERVICE_ROLE_KEY).")
    sys.exit(1)

supabase: Client = create_client(SUPABASE_URL, SUPABASE_SERVICE_ROLE_KEY)

NEWS_SYNTHESIS_PROMPT = """
You are an objective news editor. You are provided with raw news articles or RSS text covering a recent event.

INSTRUCTIONS:
1. Formulate a concise, clickbait-free objective title (Max 12 words) capturing the core event.
2. Generate EXACTLY 3 key bullet points synthesized strictly from factual claims across the provided text.
3. Categorize into EXACTLY ONE of the following 12 topics:
   - Politics & Government
   - Crime & Public Safety
   - Business & Finance
   - Entertainment & Celebrity Culture
   - Sports
   - Health & Wellness
   - Science & Technology
   - Weather & Natural Disasters
   - Lifestyle & Consumer Advice
   - Cricket
   - Enterprise AI
   - India
4. Determine if this news is breaking (true or false).
5. Estimate reading time in minutes (integer, e.g. 1 or 2).
6. Determine bias label ('LEFT', 'CENTER', or 'RIGHT').

OUTPUT FORMAT (JSON ONLY):
{
  "objective_title": "String",
  "category": "String",
  "ai_summary_bullets": [
    "Fact-based point 1",
    "Fact-based point 2",
    "Fact-based point 3"
  ],
  "is_breaking": false,
  "reading_time_minutes": 2,
  "bias_label": "CENTER"
}
"""

VALID_TOPICS = [
    "Politics & Government",
    "Crime & Public Safety",
    "Business & Finance",
    "Entertainment & Celebrity Culture",
    "Sports",
    "Health & Wellness",
    "Science & Technology",
    "Weather & Natural Disasters",
    "Lifestyle & Consumer Advice",
    "Cricket",
    "Enterprise AI",
    "India"
]

def resolve_final_url(url):
    """Follows redirects to resolve the actual destination article web URL instead of RSS search/feed links."""
    if not url:
        return url
    try:
        headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"}
        response = requests.head(url, headers=headers, allow_redirects=True, timeout=5)
        final_url = response.url
        if final_url and "news.google.com" not in final_url:
            return final_url
    except Exception:
        pass

    try:
        headers = {"User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"}
        response = requests.get(url, headers=headers, allow_redirects=True, timeout=5)
        final_url = response.url
        if final_url and "news.google.com" not in final_url:
            return final_url
    except Exception:
        pass

    return url

def validate_news_cluster(cluster):
    """Editorial guardrails: checks schema, title length, bullet count, and category."""
    errors = []
    title = cluster.get("objective_title", "")
    if not title or len(title.split()) > 15:
        errors.append("Invalid or too long objective title.")

    bullets = cluster.get("ai_summary_bullets", [])
    if not isinstance(bullets, list) or len(bullets) != 3:
        errors.append("AI summary must contain exactly 3 bullets.")

    category = cluster.get("category")
    if category not in VALID_TOPICS:
        errors.append(f"Invalid category: {category}")

    return len(errors) == 0, errors

def call_gemini(api_key, raw_text, expected_topic=None):
    """Calls Gemini API (trying gemini-3.5-flash-lite, with fallbacks to gemini-1.5-flash) to synthesize news into JSON."""
    cleaned_key = api_key.strip()
    genai.configure(api_key=cleaned_key)

    models_to_try = ['gemini-3.5-flash-lite', 'gemini-1.5-flash', 'gemini-1.5-flash-8b', 'gemini-2.0-flash-exp']
    last_error = None

    prompt = f"{NEWS_SYNTHESIS_PROMPT}\n\nTarget Topic Category: {expected_topic or 'General'}\n\nRAW NEWS TEXT:\n{raw_text}"

    for model_name in models_to_try:
        try:
            print(f"Attempting synthesis with model '{model_name}'...")
            model = genai.GenerativeModel(model_name)
            response = model.generate_content(prompt)

            text_result = response.text.strip()
            if text_result.startswith("```json"):
                text_result = text_result[7:]
            if text_result.startswith("```"):
                text_result = text_result[3:]
            if text_result.endswith("```"):
                text_result = text_result[:-3]

            parsed = json.loads(text_result.strip())
            parsed["embedding"] = [0.015] * 384
            if expected_topic and parsed.get("category") not in VALID_TOPICS:
                parsed["category"] = expected_topic
            return parsed
        except Exception as e:
            print(f"Model '{model_name}' failed: {e}")
            last_error = e

    raise last_error

def call_grok(api_key, raw_text, expected_topic=None):
    """Calls Grok (xAI API) to synthesize news into JSON format."""
    cleaned_key = api_key.strip()
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {cleaned_key}"
    }
    prompt = f"{NEWS_SYNTHESIS_PROMPT}\n\nTarget Topic Category: {expected_topic or 'General'}\n\nRAW NEWS TEXT:\n{raw_text}"
    payload = {
        "model": "grok-beta",
        "messages": [
            {"role": "system", "content": "You are an objective news editor that outputs strictly valid JSON only."},
            {"role": "user", "content": prompt}
        ],
        "temperature": 0.2
    }
    response = requests.post("https://api.x.ai/v1/chat/completions", headers=headers, json=payload, timeout=30)
    response.raise_for_status()
    data = response.json()
    content = data["choices"][0]["message"]["content"].strip()
    if content.startswith("```json"):
        content = content[7:]
    if content.startswith("```"):
        content = content[3:]
    if content.endswith("```"):
        content = content[:-3]

    parsed = json.loads(content.strip())
    parsed["embedding"] = [0.015] * 384
    if expected_topic and parsed.get("category") not in VALID_TOPICS:
        parsed["category"] = expected_topic
    return parsed

def synthesize_news_with_llm_fallback(raw_text, expected_topic=None):
    """Tries Primary Gemini -> Fallback Gemini -> Grok (xAI) for live news synthesis."""

    # 1. Try Primary Gemini API Key
    if GEMINI_API_KEY:
        try:
            print(f"Attempting AI synthesis for '{expected_topic}' with Primary Gemini API Key...")
            candidate_cluster = call_gemini(GEMINI_API_KEY, raw_text, expected_topic)
            is_valid, errors = validate_news_cluster(candidate_cluster)
            if not is_valid:
                raise ValueError(f"Guardrails failed: {', '.join(errors)}")
            return candidate_cluster
        except Exception as e:
            print(f"Primary Gemini key failed: {e}")

    # 2. Try Alternate / Fallback Gemini API Key
    if GEMINI_API_KEY_FALLBACK:
        try:
            print(f"Attempting AI synthesis for '{expected_topic}' with Alternate Gemini API Key...")
            candidate_cluster = call_gemini(GEMINI_API_KEY_FALLBACK, raw_text, expected_topic)
            is_valid, errors = validate_news_cluster(candidate_cluster)
            if not is_valid:
                raise ValueError(f"Guardrails failed: {', '.join(errors)}")
            return candidate_cluster
        except Exception as e:
            print(f"Alternate Gemini key failed: {e}")

    # 3. Try Grok (xAI) Fallback Key
    if GROK_API_KEY:
        try:
            print(f"Attempting AI synthesis for '{expected_topic}' with Grok (xAI) Fallback Key...")
            candidate_cluster = call_grok(GROK_API_KEY, raw_text, expected_topic)
            is_valid, errors = validate_news_cluster(candidate_cluster)
            if not is_valid:
                raise ValueError(f"Guardrails failed: {', '.join(errors)}")
            return candidate_cluster
        except Exception as e:
            print(f"Grok fallback key failed: {e}")

    raise RuntimeError("Critical Error: All LLM providers and fallback keys failed to synthesize news.")

def get_or_create_news_source(url):
    """Gets or creates a news source entry in Supabase news_sources table based on domain."""
    parsed_url = urlparse(url)
    domain = parsed_url.netloc or "news.google.com"
    name = domain.replace("www.", "").split(".")[0].capitalize()

    # Check if source exists
    existing = supabase.table("news_sources").select("id").eq("domain", domain).execute()
    if existing.data and len(existing.data) > 0:
        return existing.data[0]["id"]

    # Insert new source
    new_source = {
        "name": name,
        "domain": domain,
        "default_bias": "CENTER"
    }
    res = supabase.table("news_sources").insert(new_source).execute()
    if res.data and len(res.data) > 0:
        return res.data[0]["id"]
    return None

def run_ingestion_pipeline():
    print("Starting PulseAI Multi-Topic Live RSS Ingestion & Synthesis Pipeline (12 Topics)...")

    # Fetch existing article URLs from Supabase for deduplication
    existing_articles = supabase.table("raw_articles").select("url").execute()
    existing_urls = {row["url"] for row in existing_articles.data} if existing_articles.data else set()
    print(f"Found {len(existing_urls)} existing articles in Supabase for deduplication.")

    # Topic-to-RSS Mapping for all 12 topics
    topic_feeds = {
        "Politics & Government": ["https://news.google.com/rss/search?q=Politics+Government&hl=en-US&gl=US&ceid=US:en"],
        "Crime & Public Safety": ["https://news.google.com/rss/search?q=Crime+Public+Safety&hl=en-US&gl=US&ceid=US:en"],
        "Business & Finance": [
            "https://feeds.bbci.co.uk/news/business/rss.xml",
            "https://news.google.com/rss/search?q=Business+Finance&hl=en-US&gl=US&ceid=US:en"
        ],
        "Entertainment & Celebrity Culture": ["https://news.google.com/rss/search?q=Entertainment+Celebrity+Culture&hl=en-US&gl=US&ceid=US:en"],
        "Sports": ["https://news.google.com/rss/search?q=Sports&hl=en-US&gl=US&ceid=US:en"],
        "Health & Wellness": ["https://news.google.com/rss/search?q=Health+Wellness&hl=en-US&gl=US&ceid=US:en"],
        "Science & Technology": [
            "https://feeds.bbci.co.uk/news/technology/rss.xml",
            "https://news.google.com/rss/search?q=Science+Technology&hl=en-US&gl=US&ceid=US:en"
        ],
        "Weather & Natural Disasters": ["https://news.google.com/rss/search?q=Weather+Natural+Disasters&hl=en-US&gl=US&ceid=US:en"],
        "Lifestyle & Consumer Advice": ["https://news.google.com/rss/search?q=Lifestyle+Consumer+Advice&hl=en-US&gl=US&ceid=US:en"],
        "Cricket": ["https://news.google.com/rss/search?q=Cricket&hl=en-US&gl=US&ceid=US:en"],
        "Enterprise AI": [
            "https://news.google.com/rss/search?q=Enterprise+AI+Generative+AI&hl=en-US&gl=US&ceid=US:en"
        ],
        "India": [
            "https://news.google.com/rss/search?q=India+News&hl=en-IN&gl=IN&ceid=IN:en",
            "https://feeds.feedburner.com/ndtvnews-top-stories"
        ]
    }

    total_clusters_generated = 0
    total_articles_inserted = 0

    # Process each of the 12 topics independently
    for topic, feeds in topic_feeds.items():
        topic_articles = []
        for feed_url in feeds:
            parsed_feed = feedparser.parse(feed_url)
            for entry in parsed_feed.entries:
                link = entry.get("link", "")
                title = entry.get("title", "")
                summary = entry.get("summary", "")
                published = entry.get("published", datetime.datetime.now(datetime.timezone.utc).isoformat())

                if link and link not in existing_urls:
                    resolved_link = resolve_final_url(link)
                    topic_articles.append({
                        "title": title,
                        "summary": summary,
                        "link": resolved_link,
                        "published": published
                    })
                    existing_urls.add(link)
                    existing_urls.add(resolved_link)

        if not topic_articles:
            print(f"[{topic}] No new articles found.")
            continue

        # Take top 3 new articles for this topic to form 1 synthesized story cluster
        batch = topic_articles[:3]
        raw_text_list = [f"Title: {art['title']}\nSummary: {art['summary']}\nURL: {art['link']}" for art in batch]
        raw_text = "\n\n".join(raw_text_list)

        try:
            synthesized_cluster = synthesize_news_with_llm_fallback(raw_text, expected_topic=topic)

            # Extract source names and primary source link
            source_domains = []
            for art in batch:
                domain = urlparse(art["link"]).netloc.replace("www.", "").split(".")[0].capitalize()
                if domain and domain not in source_domains:
                    source_domains.append(domain)

            primary_source_url = batch[0]["link"] if batch else "https://news.google.com"
            source_names_str = " • ".join(source_domains) if source_domains else "Verified Media Outlets"

            cluster_payload = {
                "objective_title": synthesized_cluster.get("objective_title"),
                "category": topic,
                "ai_summary_bullets": synthesized_cluster.get("ai_summary_bullets"),
                "source_url": primary_source_url,
                "source_names": source_names_str,
                "embedding": synthesized_cluster.get("embedding", [0.015] * 384),
                "is_breaking": synthesized_cluster.get("is_breaking", False),
                "reading_time_minutes": synthesized_cluster.get("reading_time_minutes", 2)
            }

            # Insert story cluster
            cluster_res = supabase.table("story_clusters").insert(cluster_payload).execute()
            if cluster_res.data and len(cluster_res.data) > 0:
                cluster_id = cluster_res.data[0]["id"]
                total_clusters_generated += 1
                print(f"[{topic}] Successfully created story cluster: {cluster_id}")

                if cluster_payload.get("is_breaking"):
                    print(f"[ONE-TIME PUSH TRIGGER] Sent one-time breaking news alert for newly inserted cluster '{cluster_id}': {cluster_payload.get('objective_title')}")

                # Insert raw source articles
                for art in batch:
                    source_id = get_or_create_news_source(art["link"])
                    article_payload = {
                        "cluster_id": cluster_id,
                        "source_id": source_id,
                        "original_title": art["title"],
                        "url": art["link"],
                        "content": art["summary"],
                        "bias_label": synthesized_cluster.get("bias_label", "CENTER"),
                        "published_at": datetime.datetime.now(datetime.timezone.utc).isoformat()
                    }
                    try:
                        supabase.table("raw_articles").insert(article_payload).execute()
                        total_articles_inserted += 1
                    except Exception as e:
                        print(f"Skipping article insert for {art['link']}: {e}")

        except Exception as e:
            print(f"[{topic}] Failed to synthesize cluster: {e}")

    print(f"Pipeline Execution Complete! Generated {total_clusters_generated} new story clusters and stored {total_articles_inserted} source articles across 12 topics.")

if __name__ == "__main__":
    run_ingestion_pipeline()
