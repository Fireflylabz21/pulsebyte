# PulseAI — Supabase Database Schema Enhancements

Based on the recent enhancements (Breaking News Ticker, Estimated Reading Times, and Interactive Coverage Source URLs), here are the recommended SQL schema updates for your Supabase database.

Run this migration script in your Supabase SQL Editor (`https://supabase.com/dashboard/project/ixjzzcwvkuvlxcypwqkj/sql/new`):

---

## SQL Migration Script

```sql
-- ==========================================
-- PULSEAI SCHEMA ENHANCEMENTS MIGRATION
-- ==========================================

-- 1. Add Breaking News & Reading Time columns to story_clusters
ALTER TABLE story_clusters
ADD COLUMN IF NOT EXISTS is_breaking BOOLEAN DEFAULT false,
ADD COLUMN IF NOT EXISTS reading_time_minutes INT DEFAULT 1,
ADD COLUMN IF NOT EXISTS audio_summary_url TEXT;

-- 2. Create index on is_breaking for fast breaking news banner queries
CREATE INDEX IF NOT EXISTS story_clusters_is_breaking_idx
ON story_clusters (is_breaking)
WHERE is_breaking = true;

-- 3. Ensure raw_articles has indexes for fast coverage source lookups
CREATE INDEX IF NOT EXISTS raw_articles_cluster_id_idx
ON raw_articles (cluster_id);

CREATE INDEX IF NOT EXISTS raw_articles_source_id_idx
ON raw_articles (source_id);

-- 4. Sample SQL to mark a story as breaking news
-- UPDATE story_clusters
-- SET is_breaking = true, reading_time_minutes = 2
-- WHERE category = 'WORLD'
-- LIMIT 1;
```
