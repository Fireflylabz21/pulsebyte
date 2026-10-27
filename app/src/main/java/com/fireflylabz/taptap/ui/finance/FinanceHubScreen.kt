package com.fireflylabz.taptap.ui.finance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fireflylabz.taptap.R
import com.fireflylabz.taptap.domain.model.*

private data class IndexCardTheme(
    val gradientColors: List<Color>,
    val badgeColor: Color,
    val textColor: Color
)

private fun getIndexCardTheme(symbol: String): IndexCardTheme {
    val norm = symbol.uppercase()
    return when {
        norm.contains("SENSEX") || norm.contains("BSESN") -> IndexCardTheme(
            gradientColors = listOf(Color(0xFF1E1B4B), Color(0xFF312E81)),
            badgeColor = Color(0xFF818CF8),
            textColor = Color.White
        )
        norm.contains("NIFTY") || norm.contains("NSEI") -> IndexCardTheme(
            gradientColors = listOf(Color(0xFF064E3B), Color(0xFF065F46)),
            badgeColor = Color(0xFF34D399),
            textColor = Color.White
        )
        norm.contains("GSPC") || norm.contains("S&P") -> IndexCardTheme(
            gradientColors = listOf(Color(0xFF1B1B3A), Color(0xFF2E2E5C)),
            badgeColor = Color(0xFFF472B6),
            textColor = Color.White
        )
        norm.contains("IXIC") || norm.contains("NASDAQ") -> IndexCardTheme(
            gradientColors = listOf(Color(0xFF0C4A6E), Color(0xFF075985)),
            badgeColor = Color(0xFF38BDF8),
            textColor = Color.White
        )
        norm.contains("FTSE") -> IndexCardTheme(
            gradientColors = listOf(Color(0xFF451A03), Color(0xFF78350F)),
            badgeColor = Color(0xFFFBBF24),
            textColor = Color.White
        )
        norm.contains("N225") || norm.contains("NIKKEI") -> IndexCardTheme(
            gradientColors = listOf(Color(0xFF4C0519), Color(0xFF881337)),
            badgeColor = Color(0xFFFB7185),
            textColor = Color.White
        )
        else -> IndexCardTheme(
            gradientColors = listOf(Color(0xFF18181B), Color(0xFF27272A)),
            badgeColor = Color(0xFF38BDF8),
            textColor = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceHubScreen(
    viewModel: FinanceViewModel,
    onBackClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val indices by viewModel.indices.collectAsState()
    val gainersLosers by viewModel.gainersLosers.collectAsState()
    val macroIndicators by viewModel.macroIndicators.collectAsState()
    val commodities by viewModel.commodities.collectAsState()
    val macroCorrelations by viewModel.macroCorrelations.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Indices & Markets", "Gainers & Losers", "Macro & Commodities", "Insights")

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
                                text = "BusinessHub",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "India Markets & Intelligence",
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
                            contentDescription = "About / Info",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFF09090B)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF121216),
                contentColor = Color(0xFF38BDF8),
                edgePadding = 16.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selectedTab == index) Color(0xFF38BDF8) else Color(0xFFA1A1AA)
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> IndicesMarketsTab(indices)
                1 -> GainersLosersTab(gainersLosers)
                2 -> MacroCommoditiesTab(macroIndicators, commodities)
                3 -> InsightsTab(macroCorrelations)
            }
        }
    }
}

@Composable
fun IndicesMarketsTab(indices: List<MarketIndex>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        items(indices) { index ->
            val isPositive = index.changePct >= 0
            val cardTheme = getIndexCardTheme(index.symbol)

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(cardTheme.gradientColors))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = index.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                    color = cardTheme.textColor
                                )
                                Text(
                                    text = "${index.exchange} • ${index.symbol}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "%,.2f".format(index.price),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                                    color = cardTheme.textColor
                                )
                                Surface(
                                    color = if (isPositive) Color(0xFF065F46) else Color(0xFF7F1D1D),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${if (isPositive) "+" else ""}${index.changePct}%",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isPositive) Color(0xFF34D399) else Color(0xFFF87171),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricCell("Open", "%,.2f".format(index.open))
                            MetricCell("High", "%,.2f".format(index.high))
                            MetricCell("Low", "%,.2f".format(index.low))
                            MetricCell("RSI (14)", "%.1f".format(index.rsi14))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricCell("SMA 50", "%,.2f".format(index.sma50))
                            MetricCell("SMA 200", "%,.2f".format(index.sma200))
                            MetricCell("Volume", "%,d".format(index.volume))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GainersLosersTab(movers: List<StockMover>) {
    var selectedTimeframe by remember { mutableStateOf("Day") }
    val filteredMovers = remember(movers, selectedTimeframe) {
        movers.filter { it.timeFrame.equals(selectedTimeframe, ignoreCase = true) }
    }

    val topGainers = remember(filteredMovers) { filteredMovers.filter { it.isGainer } }
    val topLosers = remember(filteredMovers) { filteredMovers.filter { !it.isGainer } }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = selectedTimeframe == "Day",
                        onClick = { selectedTimeframe = "Day" },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Day (1D)",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp
                        )
                    }
                    SegmentedButton(
                        selected = selectedTimeframe == "Week",
                        onClick = { selectedTimeframe = "Week" },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Week (1W)",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Section: Top Gainers
        item {
            Text(
                text = "🚀 Top Gainers (${selectedTimeframe})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = Color(0xFF34D399)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(topGainers) { stock ->
            StockMoverCard(stock)
        }

        // Section: Top Losers
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "🔻 Top Losers (${selectedTimeframe})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = Color(0xFFF87171)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(topLosers) { stock ->
            StockMoverCard(stock)
        }
    }
}

@Composable
fun StockMoverCard(stock: StockMover) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = stock.companyName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${stock.symbol} • Vol: ${stock.volume}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFA1A1AA),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "₹%,.2f".format(stock.currentPrice),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                    color = Color.White,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    color = if (stock.isGainer) Color(0xFF065F46) else Color(0xFF7F1D1D),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${if (stock.isGainer) "+" else ""}${stock.changePct}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                        color = if (stock.isGainer) Color(0xFF34D399) else Color(0xFFF87171),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun MacroCommoditiesTab(macros: List<MacroIndicator>, commodities: List<CommodityItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text(
                text = "Macro Economic Indicators",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(macros) { macro ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = macro.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            color = Color(0xFF334155),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = macro.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Current Value",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA1A1AA)
                            )
                            Text(
                                text = macro.value,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp
                                ),
                                color = Color(0xFF38BDF8)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Previous",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA1A1AA)
                            )
                            Text(
                                text = macro.previousValue,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = macro.changeNote,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        ),
                        color = Color(0xFF93C5FD)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Commodities Watch & Pricing",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(commodities) { comm ->
            val isPositive = comm.changePct >= 0
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF271C10)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = comm.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                            color = Color.White
                        )
                        Text(
                            text = "${comm.category} • Unit: ${comm.unit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFBBF24)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "%,.2f".format(comm.price),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 17.sp),
                            color = Color.White
                        )
                        Surface(
                            color = if (isPositive) Color(0xFF065F46) else Color(0xFF7F1D1D),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${if (isPositive) "+" else ""}${comm.changePct}%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isPositive) Color(0xFF34D399) else Color(0xFFF87171),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Single-line tile list showing full text for Sector Insights
@Composable
fun InsightsTab(correlations: List<MacroCorrelationItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        items(correlations) { item ->
            val isPositive = item.impactType.equals("Positive", ignoreCase = true)
            
            val bgGradient = if (isPositive) {
                listOf(Color(0xFF064E3B), Color(0xFF065F46))
            } else {
                listOf(Color(0xFF7F1D1D), Color(0xFF991B1B))
            }
            val badgeBg = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444)
            val sectorTextColor = if (isPositive) Color(0xFFA7F3D0) else Color(0xFFFCA5A5)

            Card(
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(bgGradient))
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.affectedSector,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = sectorTextColor,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                color = badgeBg,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${if (isPositive) "▲ POSITIVE" else "▼ NEGATIVE"} (${item.impactMagnitude.uppercase()})",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = item.triggerEvent,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                lineHeight = 22.sp
                            ),
                            color = Color.White
                        )

                        Text(
                            text = item.explanation,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCell(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
    }
}
