const { createClient } = require('@supabase/supabase-js');

const supabaseUrl = process.env.SUPABASE_URL;
const supabaseKey = process.env.SUPABASE_SERVICE_ROLE_KEY;
const geminiApiKey = process.env.GEMINI_API_KEY;

if (!supabaseUrl || !supabaseKey) {
  console.error('Missing Supabase environment variables.');
  process.exit(1);
}

const supabase = createClient(supabaseUrl, supabaseKey);

async function runIngestion() {
  console.log('Starting PulseAI Ingestion & Gemini Synthesis Pipeline...');

  // Test inserting a synthesized test news cluster
  const testCluster = {
    objective_title: "Global Tech Markets Rally on Breakthrough AI Semiconductor Announcements",
    ai_summary_bullets: [
      "Semiconductor equities surged 4.2% following unprecedented efficiency gains in 2nm chip fabrication.",
      "Major cloud providers announced joint infrastructure commitments for sustainable data centers.",
      "Industry analysts project robust enterprise adoption across healthcare and financial sectors through Q4."
    ],
    category: "FINANCE",
    embedding: Array(384).fill(0.01) // mock 384-dim vector embedding
  };

  const { data, error } = await supabase
    .from('story_clusters')
    .insert([testCluster])
    .select();

  if (error) {
    console.error('Error inserting story cluster into Supabase:', error.message);
    process.exit(1);
  }

  console.log('Successfully inserted live story cluster into Supabase:', data);
  console.log('Ingestion test completed successfully!');
}

runIngestion();
