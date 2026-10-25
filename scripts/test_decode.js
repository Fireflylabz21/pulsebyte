const { createClient } = require('@supabase/supabase-js');

const supabaseUrl = process.env.SUPABASE_URL || "https://ixjzzcwvkuvlxcypwqkj.supabase.co";
const supabaseKey = process.env.SUPABASE_ANON_KEY || "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Iml4anp6Y3d2a3V2bHhjeXB3cWtqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTI4Mjk3NzYsImV4cCI6MjEwODQwNTc3Nn0.mNuxLbJH2SdB1BO9r1xPdD_uhagByEaEZkiQ2bylUfI";

const supabase = createClient(supabaseUrl, supabaseKey);

async function testFetchWithAnonKey() {
  console.log('Testing query with ANON key (same key used in Android app)...');

  const { data, error } = await supabase
    .from('story_clusters')
    .select('*');

  if (error) {
    console.error('ERROR querying with ANON key:', error);
    return;
  }

  console.log('Returned data count:', data.length);
  if (data.length > 0) {
    console.log('Raw JSON sample:', JSON.stringify(data[0], null, 2));
  }
}

testFetchWithAnonKey();
