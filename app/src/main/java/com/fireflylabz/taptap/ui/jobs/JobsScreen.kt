package com.fireflylabz.taptap.ui.jobs

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.fireflylabz.taptap.R
import com.fireflylabz.taptap.domain.model.JobItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    viewModel: JobsViewModel,
    onBackClick: () -> Unit,
    onAboutClick: () -> Unit = {},
    onApplyClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    var selectedJobForDetail by remember { mutableStateOf<JobItem?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }
    var filterTitle by remember { mutableStateOf("") }
    var filterCompany by remember { mutableStateOf("") }
    var filterLocation by remember { mutableStateOf("") }
    var filterSkillset by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF09090B))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF18181B), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Image(
                            painter = painterResource(R.drawable.app_logo),
                            contentDescription = "TapTap Logo",
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = "AI Jobs & Careers",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "Explore Verified Tech & AI Roles",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA1A1AA)
                            )
                        }
                    }

                    IconButton(
                        onClick = onAboutClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF18181B), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About Us",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFF09090B)
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshJobs() },
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateLeftPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                    end = paddingValues.calculateRightPadding(androidx.compose.ui.unit.LayoutDirection.Ltr),
                    bottom = 0.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // 1. Sign Up for Weekly Alerts Banner
                WeeklyAlertsBannerCard(
                    onClick = { onApplyClick("https://tally.so/r/QKgdWX") }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by title, company, skills...", color = Color(0xFFA1A1AA)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF38BDF8)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                viewModel.updateFilters(query = "", title = filterTitle, company = filterCompany, location = filterLocation, skillset = filterSkillset)
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFFA1A1AA))
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.updateFilters(query = searchQuery, title = filterTitle, company = filterCompany, location = filterLocation, skillset = filterSkillset)
                            keyboardController?.hide()
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF27272A),
                        focusedContainerColor = Color(0xFF18181B),
                        unfocusedContainerColor = Color(0xFF18181B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Advanced Filters Panel
                if (showFilters) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Advanced Filters", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            
                            OutlinedTextField(
                                value = filterTitle,
                                onValueChange = { filterTitle = it },
                                placeholder = { Text("Filter by Job Title", color = Color(0xFFA1A1AA)) },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        viewModel.updateFilters(query = searchQuery, title = filterTitle, company = filterCompany, location = filterLocation, skillset = filterSkillset)
                                        keyboardController?.hide()
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF121216), unfocusedContainerColor = Color(0xFF121216))
                            )

                            OutlinedTextField(
                                value = filterCompany,
                                onValueChange = { filterCompany = it },
                                placeholder = { Text("Filter by Company", color = Color(0xFFA1A1AA)) },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        viewModel.updateFilters(query = searchQuery, title = filterTitle, company = filterCompany, location = filterLocation, skillset = filterSkillset)
                                        keyboardController?.hide()
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF121216), unfocusedContainerColor = Color(0xFF121216))
                            )

                            OutlinedTextField(
                                value = filterLocation,
                                onValueChange = { filterLocation = it },
                                placeholder = { Text("Filter by Location", color = Color(0xFFA1A1AA)) },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        viewModel.updateFilters(query = searchQuery, title = filterTitle, company = filterCompany, location = filterLocation, skillset = filterSkillset)
                                        keyboardController?.hide()
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF121216), unfocusedContainerColor = Color(0xFF121216))
                            )

                            OutlinedTextField(
                                value = filterSkillset,
                                onValueChange = { filterSkillset = it },
                                placeholder = { Text("Filter by Skillset (e.g. Python, Kotlin)", color = Color(0xFFA1A1AA)) },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        viewModel.updateFilters(query = searchQuery, title = filterTitle, company = filterCompany, location = filterLocation, skillset = filterSkillset)
                                        keyboardController?.hide()
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color(0xFF121216), unfocusedContainerColor = Color(0xFF121216))
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = {
                                    filterTitle = ""
                                    filterCompany = ""
                                    filterLocation = ""
                                    filterSkillset = ""
                                    searchQuery = ""
                                    viewModel.updateFilters()
                                }) {
                                    Text("Reset Filters", color = Color(0xFF38BDF8))
                                }

                                Button(
                                    onClick = {
                                        viewModel.updateFilters(query = searchQuery, title = filterTitle, company = filterCompany, location = filterLocation, skillset = filterSkillset)
                                        keyboardController?.hide()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                                ) {
                                    Text("Apply Filters", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (val state = uiState) {
                    is JobsUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF38BDF8))
                        }
                    }
                    is JobsUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "⚠️ Unable to load jobs",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFF87171)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Please ensure Supabase RLS read permissions are configured on the 'jobs' table.\n\n${state.message}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFA1A1AA),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.fetchJobs() },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                                ) {
                                    Text("Retry", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    is JobsUiState.Success -> {
                        if (state.filteredJobs.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("No active jobs found in database.", color = Color(0xFFA1A1AA), textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = { viewModel.fetchJobs() }) {
                                        Text("Tap to Refresh", color = Color(0xFF38BDF8))
                                    }
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 4.dp)
                            ) {
                                items(state.filteredJobs) { job ->
                                    JobCard(job = job, onClick = { selectedJobForDetail = job })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Full-Page Pop Up of Job Details
    selectedJobForDetail?.let { job ->
        val context = LocalContext.current
        Dialog(
            onDismissRequest = { selectedJobForDetail = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFF09090B)
            ) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Job Details", fontWeight = FontWeight.Bold) },
                            navigationIcon = {
                                IconButton(onClick = { selectedJobForDetail = null }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        val expStr = if (job.minExperience != null && job.maxExperience != null) "${job.minExperience}-${job.maxExperience} yrs experience" else "Not specified"
                                        val shareText = "🚀 *${job.title}* at *${job.company}*\n📍 ${job.location ?: "Remote"}\n⏱️ $expStr\n\n📝 *Required Skills*: ${job.requiredSkills.joinToString(", ")}\n\n🔗 *Apply Here*: ${job.applyUrl}\n\nShared via TapTap AI Jobs"
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "Share Job via")
                                        context.startActivity(shareIntent)
                                    }
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share Job", tint = Color(0xFF38BDF8))
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF09090B), titleContentColor = Color.White),
                            windowInsets = WindowInsets(0, 0, 0, 0)
                        )
                    },
                    containerColor = Color(0xFF09090B)
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title & Company Header Card
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = job.title,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        lineHeight = 28.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = job.company,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = Color(0xFF38BDF8)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        if (!job.location.isNullOrBlank()) {
                                            Surface(color = Color(0xFF27272A), shape = RoundedCornerShape(8.dp)) {
                                                Text(
                                                    text = "📍 ${job.location}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                        if (!job.workStyle.isNullOrBlank()) {
                                            Surface(color = Color(0xFF1E3A8A), shape = RoundedCornerShape(8.dp)) {
                                                Text(
                                                    text = "💼 ${job.workStyle}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = Color(0xFF93C5FD),
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (job.minExperience != null || job.maxExperience != null) {
                                        Surface(color = Color(0xFF065F46), shape = RoundedCornerShape(8.dp)) {
                                            Text(
                                                text = "⏱️ ${job.minExperience ?: 0}-${job.maxExperience ?: 0} yrs",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Color(0xFF34D399),
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Required Skills Card
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Required Skillsets",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFFBBF24)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = job.requiredSkills.joinToString(" • "),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp, lineHeight = 22.sp),
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }

                        // Summary Card
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Summary",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF38BDF8)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = job.descriptionText,
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                                    color = Color(0xFFA1A1AA)
                                )
                            }
                        }

                        // Apply here Button Card
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val url = job.applyUrl
                                    selectedJobForDetail = null
                                    onApplyClick(url)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Apply here",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF38BDF8)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = job.applyUrl,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF93C5FD),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Open Link",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyAlertsBannerCard(
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF065F46), Color(0xFF047857), Color(0xFF10B981))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "🔔 Sign up for weekly alerts",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Never miss a top AI or tech opening. Get weekly curated job drops delivered.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFE2E8F0)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    color = Color.White,
                    shape = CircleShape
                ) {
                    Box(
                        modifier = Modifier.size(38.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.NotificationsActive,
                            contentDescription = "Alerts",
                            tint = Color(0xFF065F46),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JobCard(job: JobItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = job.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = job.company,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF38BDF8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (job.minExperience != null || job.maxExperience != null) {
                    Surface(
                        color = Color(0xFF065F46),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${job.minExperience ?: 0}-${job.maxExperience ?: 0} yrs",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF34D399),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!job.location.isNullOrBlank()) {
                    Text(
                        text = "📍 ${job.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA1A1AA),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (job.requiredSkills.isNotEmpty()) {
                    Text(
                        text = "⚡ ${job.requiredSkills.firstOrNull() ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFBBF24),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
