package com.fireflylabz.pulsebyte.ui.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fireflylabz.pulsebyte.domain.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceHubScreen(
    viewModel: FinanceViewModel,
    onBackClick: () -> Unit
) {
    val indices by viewModel.indices.collectAsState()
    val stockScreener by viewModel.stockScreener.collectAsState()
    val mutualFunds by viewModel.mutualFunds.collectAsState()
    val macroIndicators by viewModel.macroIndicators.collectAsState()
    val commodities by viewModel.commodities.collectAsState()
    val financialTips by viewModel.financialTips.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Indices & Markets", "Screeners & Funds", "Macro & Commodities", "Calculators & Tips")

    // DCF Calculator State
    var fcfInput by remember { mutableStateOf("100.0") }
    var growthInput by remember { mutableStateOf("12.0") }
    var discountInput by remember { mutableStateOf("9.0") }
    var terminalInput by remember { mutableStateOf("3.0") }
    var sharesInput by remember { mutableStateOf("50.0") }
    var dcfResult by remember { mutableStateOf<Double?>(null) }

    // PEG Calculator State
    var peInput by remember { mutableStateOf("22.5") }
    var pegGrowthInput by remember { mutableStateOf("15.0") }
    var pegResult by remember { mutableStateOf<Double?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Financial Intelligence Hub",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                            color = Color.White
                        )
                        Text(
                            text = "Global Markets, Screeners & Quantitative Telemetry",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA1A1AA)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF09090B))
            )
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

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTab) {
                0 -> IndicesMarketsTab(indices)
                1 -> ScreenersFundsTab(stockScreener, mutualFunds)
                2 -> MacroCommoditiesTab(macroIndicators, commodities)
                3 -> CalculatorsTipsTab(
                    fcfInput, { fcfInput = it },
                    growthInput, { growthInput = it },
                    discountInput, { discountInput = it },
                    terminalInput, { terminalInput = it },
                    sharesInput, { sharesInput = it },
                    dcfResult, {
                        val f = fcfInput.toDoubleOrNull() ?: 0.0
                        val g = growthInput.toDoubleOrNull() ?: 0.0
                        val d = discountInput.toDoubleOrNull() ?: 0.0
                        val tg = terminalInput.toDoubleOrNull() ?: 0.0
                        val s = sharesInput.toDoubleOrNull() ?: 1.0
                        dcfResult = viewModel.calculateDcf(f, g, d, tg, s)
                    },
                    peInput, { peInput = it },
                    pegGrowthInput, { pegGrowthInput = it },
                    pegResult, {
                        val pe = peInput.toDoubleOrNull() ?: 0.0
                        val gr = pegGrowthInput.toDoubleOrNull() ?: 1.0
                        pegResult = viewModel.calculatePeg(pe, gr)
                    },
                    financialTips
                )
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
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Global Capital Markets Telemetry",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Real-time index feeds, intraday OHLCV telemetry, and technical momentum indicators (SMA 50/200, RSI 14).",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA1A1AA)
                    )
                }
            }
        }

        items(indices) { index ->
            val isPositive = index.changePct >= 0
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = index.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${index.exchange} • Symbol: ${index.symbol}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA1A1AA)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "%,.2f".format(index.price),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Surface(
                                color = if (isPositive) Color(0xFF065F46) else Color(0xFF7F1D1D),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${if (isPositive) "+" else ""}${index.changePct}%",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isPositive) Color(0xFF34D399) else Color(0xFFF87171),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFF27272A))
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

@Composable
fun ScreenersFundsTab(stocks: List<StockScreenerItem>, funds: List<MutualFundItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text(
                text = "Top Performing Stocks (Monthly Leadership)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ranked by rolling 30-day performance score, volume acceleration, and Sharpe ratio.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFA1A1AA)
            )
        }

        items(stocks) { stock ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFF3B82F6),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "#${stock.rank}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = stock.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Text(
                                    text = "${stock.marketCapCategory} • ${stock.symbol}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFA1A1AA)
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+${stock.monthlyReturnPct}%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF34D399)
                            )
                            Text(
                                text = "Sharpe: ${stock.sharpeRatio}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA1A1AA)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Volume Spike Factor: ${stock.volumeSpikeFactor}x normal volume",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF38BDF8)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Top Performing Mutual Funds & ETFs",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        items(funds) { fund ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = fund.fundName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "${fund.category} • NAV: ₹${fund.nav}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFA1A1AA)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+${fund.monthlyReturnPct}% (1M)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF34D399)
                            )
                            Text(
                                text = "3Y CAGR: ${fund.threeYrCagrPct}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MacroCommoditiesTab(macros: List<MacroIndicator>, commodities: List<CommodityItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text(
                text = "Central Banking & Macroeconomics",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(macros) { macro ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = macro.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = macro.changeNote,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF38BDF8)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = macro.value,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "Prev: ${macro.previousValue}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA1A1AA)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Commodities Watch & Pricing",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        items(commodities) { comm ->
            val isPositive = comm.changePct >= 0
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = comm.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "${comm.category} • Unit: ${comm.unit}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA1A1AA)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "%,.2f".format(comm.price),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "${if (isPositive) "+" else ""}${comm.changePct}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isPositive) Color(0xFF34D399) else Color(0xFFF87171)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorsTipsTab(
    fcf: String, onFcfChange: (String) -> Unit,
    growth: String, onGrowthChange: (String) -> Unit,
    discount: String, onDiscountChange: (String) -> Unit,
    terminal: String, onTerminalChange: (String) -> Unit,
    shares: String, onSharesChange: (String) -> Unit,
    dcfResult: Double?, onCalculateDcf: () -> Unit,
    pe: String, onPeChange: (String) -> Unit,
    pegGrowth: String, onPegGrowthChange: (String) -> Unit,
    pegResult: Double?, onCalculatePeg: () -> Unit,
    tips: List<FinancialTip>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Discounted Cash Flow (DCF) Calculator",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fcf,
                        onValueChange = onFcfChange,
                        label = { Text("Free Cash Flow (\$M)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = growth,
                            onValueChange = onGrowthChange,
                            label = { Text("Growth (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = discount,
                            onValueChange = onDiscountChange,
                            label = { Text("Discount (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = terminal,
                            onValueChange = onTerminalChange,
                            label = { Text("Terminal (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = shares,
                            onValueChange = onSharesChange,
                            label = { Text("Shares (M)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onCalculateDcf,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                    ) {
                        Text("Calculate Intrinsic Value per Share", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    if (dcfResult != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = Color(0xFF065F46),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "Estimated Intrinsic Value", style = MaterialTheme.typography.bodySmall, color = Color.White)
                                Text(text = "$${"%.2f".format(dcfResult)} per share", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = Color(0xFF34D399))
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PEG Ratio Analysis",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = pe,
                            onValueChange = onPeChange,
                            label = { Text("P/E Ratio") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pegGrowth,
                            onValueChange = onPegGrowthChange,
                            label = { Text("Growth Rate (%)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onCalculatePeg,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF38BDF8))
                    ) {
                        Text("Calculate PEG Ratio", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    if (pegResult != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "PEG Ratio Result", style = MaterialTheme.typography.bodySmall, color = Color.White)
                                Text(text = "%.2f (${if (pegResult < 1.0) "Undervalued / Growth at reasonable price" else "Fully Valued"})".format(pegResult), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF38BDF8))
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Investment Strategies & Financial Literacy",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        items(tips) { tip ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18181B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tip.title,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = tip.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF38BDF8),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = tip.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFA1A1AA)
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCell(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color(0xFFA1A1AA))
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
    }
}
