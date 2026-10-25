const { createClient } = require('@supabase/supabase-js');

const supabaseUrl = process.env.SUPABASE_URL || "https://ixjzzcwvkuvlxcypwqkj.supabase.co";
const supabaseKey = process.env.SUPABASE_SERVICE_ROLE_KEY || "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml4anp6Y3d2a3V2bHhjeXB3cWtqIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc5MjgyOTc3NiwiZXhwIjoyMTA4NDA1Nzc2fQ.Z91MKaieN-yBUYM-U_5P2HJ4h10UJHC3MqgQa8Fg3UU";

const supabase = createClient(supabaseUrl, supabaseKey);

async function checkSupabase() {
  console.log('Checking Supabase database connection and records...');

  const { data, error } = await supabase
    .from('story_clusters')
    .select('*');

  if (error) {
    console.error('Permission or database error:', error.message);
    return;
  }

  console.log(`Successfully fetched ${data.length} story clusters from Supabase!`);
  console.log('Records:', JSON.stringify(data, null, 2));
}

checkSupabase();
