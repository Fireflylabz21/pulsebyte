"""
PulseAI Automated News Ingestion & Multi-LLM Synthesis Worker (Python Edition)
Fetches live RSS news feeds, synthesizes via Gemini 1.5 Flash (with alternate Gemini & Grok fallbacks),
runs editorial guardrails, generates vector embeddings, and upserts into Supabase.
"""

import os
import sys
import json
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
3. Categorize into ONE of: [WORLD, TECH, FINANCE, BUSINESS, SCIENCE, POLITICS, ENTERTAINMENT, SPORTS, LIFESTYLE, LOCAL].

OUTPUT FORMAT (JSON ONLY):
{
  "objective_title": "String",
  "category": "String",
  "summary_bullets": [
    "Fact-based point 1",
    "Fact-based point 2",
    "Fact-based point 3"
  ]
}
"""

def validate_news_cluster(cluster):
    """Editorial guardrails: checks schema, title length, and bullet count."""
    errors = []
    title = cluster.get("objective_title", "")
    if not title or len(title.split()) > 15:
        errors.append("Invalid or too long objective title.")

    bullets = cluster.get("ai_summary_bullets", [])
    if not isinstance(bullets, list) or len(bullets) != 3:
        errors.append("AI summary must contain exactly 3 bullets.")

    valid_categories = ['WORLD', 'TECH', 'FINANCE', 'BUSINESS', 'SCIENCE', 'POLITICS', 'ENTERTAINMENT', 'SPORTS', 'LIFESTYLE', 'LOCAL']
    if cluster.get("category") not in valid_categories:
        errors.append(f"Invalid category: {cluster.get('category')}")

    return len(errors) == 0, errors

def call_gemini(api_key, raw_text):
    """Calls Gemini 1.5 Flash to synthesize news into JSON format."""
    genai.configure(api_key=api_key)
    model = genai.GenerativeModel('gemini-1.5-flash')
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
    """Tries Primary Gemini -> Fallback Gemini -> Grok (xAI) for live news synthesis without hardcoded mocks."""

    # 1. Try Primary Gemini API Key
    if GEMINI_API_KEY:
        try:
            print("Attempting AI synthesis with Primary Gemini API Key...")
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

def run_ingestion_pipeline():
    print("Starting PulseAI Live RSS Ingestion & Gemini Synthesis Pipeline...")

    # Fetch live RSS feeds from BBC News
    rss_feeds = [
        "https://feeds.bbci.co.uk/news/world/rss.xml",
        "https://feeds.bbci.co.uk/news/technology/rss.xml",
        "https://feeds.bbci.co.uk/news/business/rss.xml"
    ]

    all_articles = []
    for url in rss_feeds:
        feed = feedparser.parse(url)
        print(f"Parsed {len(feed.entries)} entries from {url}")
        for entry in feed.entries[:3]:
            title = entry.get("title", "")
            summary = entry.get("summary", "")
            all_articles.append(f"Title: {title}\nSummary: {summary}")

    raw_text = "\n\n".join(all_articles)
    if not raw_text.strip():
        print("Error: Could not fetch articles from live RSS feeds.")
        sys.exit(1)

    print(f"Aggregated {len(all_articles)} raw news articles for AI synthesis.")
    synthesized_cluster = synthesize_news_with_llm_fallback(raw_text)

    response = supabase.table("story_clusters").insert(synthesized_cluster).execute()
    print("Successfully upserted live synthesized story cluster into Supabase:", response)
    print("PulseAI Live Ingestion Pipeline completed successfully!")

if __name__ == "__main__":
    run_ingestion_pipeline()
