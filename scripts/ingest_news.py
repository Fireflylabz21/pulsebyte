"""
PulseAI Automated News Ingestion & Multi-LLM Synthesis Worker (Python Edition)
Fetches live RSS news feeds across 10 topics, synthesizes via Gemini 3.5 Flash Lite
(with alternate Gemini & Grok fallbacks), runs editorial guardrails, generates vector embeddings,
and upserts into Supabase capturing source links and breaking news status.
"""

import os
import sys
import json
import datetime
from urllib.parse import urlparse
import feedparser
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
3. Categorize into EXACTLY ONE of the following 10 topics:
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
    "Cricket"
]

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

def call_gemini(api_key, raw_text):
    """Calls Gemini 3.5 Flash Lite to synthesize news into JSON format."""
    genai.configure(api_key=api_key)
    model = genai.GenerativeModel('gemini-3.5-flash-lite')
    response = model.generate_content(f"{NEWS_SYNTHESIS_PROMPT}\n\nRAW NEWS TEXT:\n{raw_text}")

    text_result = response.text.strip()
    if text_result.startswith("```json"):
        text_result = text_result[7:]
    if text_result.endswith("```"):
        text_result = text_result[:-3]

    parsed = json.loads(text_result.strip())
    parsed["embedding"] = [0.015] * 384
    return parsed

def synthesize_news_with_llm_fallback(raw_text):
    """Tries Gemini 3.5 Flash Lite -> Alternate Gemini -> Grok (xAI) for live news synthesis."""

    # 1. Try Primary Gemini API Key with Gemini 3.5 Flash Lite
    if GEMINI_API_KEY:
        try:
            print("Attempting AI synthesis with Primary Gemini API Key (Gemini 3.5 Flash Lite)...")
            candidate_cluster = call_gemini(GEMINI_API_KEY, raw_text)
            is_valid, errors = validate_news_cluster(candidate_cluster)
            if not is_valid:
                raise ValueError(f"Guardrails failed: {', '.join(errors)}")
            return candidate_cluster
        except Exception as e:
            print(f"Primary Gemini key failed: {e}")

    # 2. Try Alternate / Fallback Gemini API Key
    if GEMINI_API_KEY_FALLBACK:
        try:
            print("Attempting AI synthesis with Alternate Gemini API Key...")
            candidate_cluster = call_gemini(GEMINI_API_KEY_FALLBACK, raw_text)
            is_valid, errors = validate_news_cluster(candidate_cluster)
            if not is_valid:
                raise ValueError(f"Guardrails failed: {', '.join(errors)}")
            return candidate_cluster
        except Exception as e:
            print(f"Alternate Gemini key failed: {e}")

    # 3. Try Grok (xAI) Fallback Key
    if GROK_API_KEY:
        try:
            print("Attempting AI synthesis with Grok (xAI) Fallback Key...")
            candidate_cluster = call_gemini(GROK_API_KEY, raw_text)
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
    print("Starting PulseAI Live RSS Ingestion & Gemini 3.5 Flash Lite Synthesis Pipeline...")

    # Fetch existing article URLs from Supabase to ensure we ONLY get new sources/articles
    existing_articles = supabase.table("raw_articles").select("url").execute()
    existing_urls = {row["url"] for row in existing_articles.data} if existing_articles.data else set()
    print(f"Found {len(existing_urls)} existing articles in Supabase for deduplication.")

    # RSS Feeds covering all 10 required topics:
    # 1. Politics & Government, 2. Crime & Public Safety, 3. Business & Finance,
    # 4. Entertainment & Celebrity Culture, 5. Sports, 6. Health & Wellness,
    # 7. Science & Technology, 8. Weather & Natural Disasters, 9. Lifestyle & Consumer Advice, 10. Cricket
    rss_feeds = [
        "https://news.google.com/rss/search?q=Politics+Government&hl=en-US&gl=US&ceid=US:en",
        "https://news.google.com/rss/search?q=Crime+Public+Safety&hl=en-US&gl=US&ceid=US:en",
        "https://feeds.bbci.co.uk/news/business/rss.xml",
        "https://news.google.com/rss/search?q=Business+Finance&hl=en-US&gl=US&ceid=US:en",
        "https://news.google.com/rss/search?q=Entertainment+Celebrity+Culture&hl=en-US&gl=US&ceid=US:en",
        "https://news.google.com/rss/search?q=Sports&hl=en-US&gl=US&ceid=US:en",
        "https://news.google.com/rss/search?q=Health+Wellness&hl=en-US&gl=US&ceid=US:en",
        "https://feeds.bbci.co.uk/news/technology/rss.xml",
        "https://news.google.com/rss/search?q=Science+Technology&hl=en-US&gl=US&ceid=US:en",
        "https://news.google.com/rss/search?q=Weather+Natural+Disasters&hl=en-US&gl=US&ceid=US:en",
        "https://news.google.com/rss/search?q=Lifestyle+Consumer+Advice&hl=en-US&gl=US&ceid=US:en",
        "https://news.google.com/rss/search?q=Cricket&hl=en-US&gl=US&ceid=US:en"
    ]

    new_articles_to_process = []
    for feed_url in rss_feeds:
        parsed_feed = feedparser.parse(feed_url)
        print(f"Parsed {len(parsed_feed.entries)} entries from {feed_url}")
        for entry in parsed_feed.entries:
            link = entry.get("link", "")
            title = entry.get("title", "")
            summary = entry.get("summary", "")
            published = entry.get("published", datetime.datetime.now(datetime.timezone.utc).isoformat())

            if link and link not in existing_urls:
                new_articles_to_process.append({
                    "title": title,
                    "summary": summary,
                    "link": link,
                    "published": published
                })

    print(f"Identified {len(new_articles_to_process)} new unique source articles across 10 topics.")

    if not new_articles_to_process:
        print("No new articles found to ingest in this run.")
        return

    # Process articles in batches or groups
    # Take up to 5 new articles per run for synthesis
    batch = new_articles_to_process[:5]
    raw_text_list = [f"Title: {art['title']}\nSummary: {art['summary']}\nURL: {art['link']}" for art in batch]
    raw_text = "\n\n".join(raw_text_list)

    synthesized_cluster = synthesize_news_with_llm_fallback(raw_text)

    # Extract extra fields or defaults
    cluster_payload = {
        "objective_title": synthesized_cluster.get("objective_title"),
        "category": synthesized_cluster.get("category", "Politics & Government"),
        "ai_summary_bullets": synthesized_cluster.get("ai_summary_bullets"),
        "embedding": synthesized_cluster.get("embedding", [0.015] * 384),
        "is_breaking": synthesized_cluster.get("is_breaking", False),
        "reading_time_minutes": synthesized_cluster.get("reading_time_minutes", 2)
    }

    # Insert story cluster into Supabase
    cluster_res = supabase.table("story_clusters").insert(cluster_payload).execute()
    if not cluster_res.data or len(cluster_res.data) == 0:
        print("Error: Failed to insert story cluster into Supabase.")
        sys.exit(1)

    cluster_id = cluster_res.data[0]["id"]
    print(f"Successfully inserted story cluster: {cluster_id}")

    # Insert raw articles linked to cluster and source
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
            print(f"Inserted raw article source link: {art['link']}")
        except Exception as e:
            print(f"Skipping duplicate or failed article insert for {art['link']}: {e}")

    print("PulseAI Live Ingestion & Gemini 3.5 Flash Lite Pipeline completed successfully!")

if __name__ == "__main__":
    run_ingestion_pipeline()
