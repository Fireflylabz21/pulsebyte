# PulseAI — Supabase Agent Ingestion & RLS Policy Guide

## 1. How the Agent Updates the Supabase DB
By default in Supabase (PostgreSQL), requests authenticated with the **`service_role`** key **completely bypass Row Level Security (RLS)**.

When your GitHub Actions cron job (`ingest_news.py`) initializes Supabase with `SUPABASE_SERVICE_ROLE_KEY`, it acts with admin privileges, allowing it to successfully `INSERT`, `UPDATE`, and `DELETE` story clusters and raw articles without tripping RLS violations.

*(Meanwhile, your mobile app uses the public `anon` key, which is restricted strictly to `SELECT` read access based on your public RLS policies).*

---

## 2. Updated SQL Script with Explicit Ingestion & Write Policies

If you want to explicitly define policies for backend workers or authenticated roles in Supabase, run this SQL script in your Supabase SQL Editor:

```sql
-- =====================================================================
-- PULSEAI SUPABASE INGESTION & RLS SECURITY POLICIES
-- =====================================================================

-- 1. Enable RLS on all tables
ALTER TABLE news_sources ENABLE ROW LEVEL SECURITY;
ALTER TABLE story_clusters ENABLE ROW LEVEL SECURITY;
ALTER TABLE raw_articles ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_saved_stories ENABLE ROW LEVEL SECURITY;

-- 2. Drop existing policies to prevent conflicts
DROP POLICY IF EXISTS "Allow public read access on news_sources" ON news_sources;
DROP POLICY IF EXISTS "Allow public read access on story_clusters" ON story_clusters;
DROP POLICY IF EXISTS "Allow public read access on raw_articles" ON raw_articles;
DROP POLICY IF EXISTS "Allow service role full access on news_sources" ON news_sources;
DROP POLICY IF EXISTS "Allow service role full access on story_clusters" ON story_clusters;
DROP POLICY IF EXISTS "Allow service role full access on raw_articles" ON raw_articles;

-- 3. Public Read Policies (For Mobile App Readers)
CREATE POLICY "Allow public read access on news_sources"
ON news_sources FOR SELECT
USING (true);

CREATE POLICY "Allow public read access on story_clusters"
ON story_clusters FOR SELECT
USING (true);

CREATE POLICY "Allow public read access on raw_articles"
ON raw_articles FOR SELECT
USING (true);

-- 4. Ingestion / Write Policies for Backend Agents (Service Role / Admin)
-- Note: service_role already bypasses RLS by default in Supabase,
-- but adding explicit TO service_role policies ensures strict role-based access control.

CREATE POLICY "Allow service role full access on news_sources"
ON news_sources
TO service_role
USING (true)
WITH CHECK (true);

CREATE POLICY "Allow service role full access on story_clusters"
ON story_clusters
TO service_role
USING (true)
WITH CHECK (true);

CREATE POLICY "Allow service role full access on raw_articles"
ON raw_articles
TO service_role
USING (true)
WITH CHECK (true);
```
