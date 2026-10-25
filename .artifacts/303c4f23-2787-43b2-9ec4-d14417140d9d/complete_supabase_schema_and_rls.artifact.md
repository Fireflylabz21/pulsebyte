# PulseAI — Complete Supabase Database Schema, Enhancements & RLS Policies

Copy and run the following complete SQL script in your Supabase SQL Editor (`https://supabase.com/dashboard/project/ixjzzcwvkuvlxcypwqkj/sql/new`) to set up or update your production database.

---

```sql
-- =====================================================================
-- PULSEAI COMPLETE SUPABASE DATABASE SCHEMA & RLS MIGRATION SCRIPT
-- =====================================================================

-- 1. EXTENSIONS
CREATE EXTENSION IF NOT EXISTS vector;


-- 2. TABLES CREATION

-- Media Outlets & Bias Classifications
CREATE TABLE IF NOT EXISTS news_sources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    domain VARCHAR(200) UNIQUE NOT NULL,
    default_bias VARCHAR(20) CHECK (default_bias IN ('LEFT', 'CENTER', 'RIGHT')),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Story Clusters (Aggregated Events with AI Summaries & Enhancements)
CREATE TABLE IF NOT EXISTS story_clusters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    objective_title TEXT NOT NULL,
    ai_summary_bullets TEXT[] NOT NULL,
    category VARCHAR(50) NOT NULL,
    embedding vector(384),
    is_breaking BOOLEAN DEFAULT false,
    reading_time_minutes INT DEFAULT 1,
    audio_summary_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Individual Articles Linked to Clusters & Sources
CREATE TABLE IF NOT EXISTS raw_articles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cluster_id UUID REFERENCES story_clusters(id) ON DELETE SET NULL,
    source_id UUID REFERENCES news_sources(id),
    original_title TEXT NOT NULL,
    url TEXT UNIQUE NOT NULL,
    content TEXT,
    bias_label VARCHAR(20) CHECK (bias_label IN ('LEFT', 'CENTER', 'RIGHT')),
    published_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- User Saved / Bookmarked Stories
CREATE TABLE IF NOT EXISTS user_saved_stories (
    user_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    cluster_id UUID REFERENCES story_clusters(id) ON DELETE CASCADE,
    saved_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (user_id, cluster_id)
);


-- 3. INDEXES FOR PERFORMANCE & VECTOR SEARCH
CREATE INDEX IF NOT EXISTS story_clusters_embedding_idx ON story_clusters
USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

CREATE INDEX IF NOT EXISTS story_clusters_is_breaking_idx
ON story_clusters (is_breaking)
WHERE is_breaking = true;

CREATE INDEX IF NOT EXISTS raw_articles_cluster_id_idx
ON raw_articles (cluster_id);

CREATE INDEX IF NOT EXISTS raw_articles_source_id_idx
ON raw_articles (source_id);


-- 4. ROW LEVEL SECURITY (RLS) ENABLEMENT
ALTER TABLE news_sources ENABLE ROW LEVEL SECURITY;
ALTER TABLE story_clusters ENABLE ROW LEVEL SECURITY;
ALTER TABLE raw_articles ENABLE ROW LEVEL SECURITY;
ALTER TABLE user_saved_stories ENABLE ROW LEVEL SECURITY;


-- 5. RLS POLICIES

-- Drop existing policies if re-running migration
DROP POLICY IF EXISTS "Allow public read access on news_sources" ON news_sources;
DROP POLICY IF EXISTS "Allow public read access on story_clusters" ON story_clusters;
DROP POLICY IF EXISTS "Allow public read access on raw_articles" ON raw_articles;
DROP POLICY IF EXISTS "Users can view their own saved stories" ON user_saved_stories;
DROP POLICY IF EXISTS "Users can insert their own saved stories" ON user_saved_stories;
DROP POLICY IF EXISTS "Users can remove their own saved stories" ON user_saved_stories;

-- Create fresh public read policies
CREATE POLICY "Allow public read access on news_sources"
ON news_sources FOR SELECT
USING (true);

CREATE POLICY "Allow public read access on story_clusters"
ON story_clusters FOR SELECT
USING (true);

CREATE POLICY "Allow public read access on raw_articles"
ON raw_articles FOR SELECT
USING (true);

-- Create user-restricted bookmark policies
CREATE POLICY "Users can view their own saved stories"
ON user_saved_stories FOR SELECT
USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own saved stories"
ON user_saved_stories FOR INSERT
WITH CHECK (auth.uid() = user_id);

CREATE POLICY "Users can remove their own saved stories"
ON user_saved_stories FOR DELETE
USING (auth.uid() = user_id);
```
