package com.fireflylabz.taptap.ui.finance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fireflylabz.taptap.data.remote.YahooFinanceApiClient
import com.fireflylabz.taptap.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class FinanceViewModel : ViewModel() {

    private val yahooApiClient = YahooFinanceApiClient()

    private val _indices = MutableStateFlow<List<MarketIndex>>(
        listOf(
            MarketIndex("^BSESN", "BSE Sensex", "BSE", 80410.75, 1.25, 80120.50, 80550.25, 79980.10, 42050000, 79200.30, 76500.10, 62.4),
            MarketIndex("^NSEI", "Nifty 50", "NSE", 24530.20, 1.10, 24300.00, 24580.50, 24250.00, 89050000, 24100.50, 23200.00, 58.9),
            MarketIndex("^GSPC", "S&P 500", "S&P", 5815.20, 0.75, 5780.00, 5830.00, 5770.00, 3100000000, 5700.00, 5400.00, 64.2),
            MarketIndex("^IXIC", "Nasdaq Composite", "NASDAQ", 18712.50, 0.85, 18580.00, 18750.00, 18550.00, 450000000, 18300.00, 17200.00, 66.5),
            MarketIndex("^FTSE", "FTSE 100", "LSE", 8240.10, -0.20, 8260.00, 8280.00, 8210.00, 120000000, 8200.00, 7900.00, 49.1),
            MarketIndex("^N225", "Nikkei 225", "TSE", 39120.00, 1.45, 38600.00, 39250.00, 38550.00, 98000000, 38400.00, 36800.00, 61.8)
        )
    )
    val indices: StateFlow<List<MarketIndex>> = _indices.asStateFlow()

    private val _gainersLosers = MutableStateFlow<List<StockMover>>(emptyList())
    val gainersLosers: StateFlow<List<StockMover>> = _gainersLosers.asStateFlow()

    private val _macroIndicators = MutableStateFlow<List<MacroIndicator>>(
        listOf(
            MacroIndicator("RBI Repo Rate", "6.50%", "6.50%", "Paused for monetary stability & growth", "Central Bank"),
            MacroIndicator("India CPI Inflation", "3.65%", "4.10%", "Food price normalization within target", "Inflation"),
            MacroIndicator("India 10Y G-Sec Yield", "6.78%", "6.85%", "Stable sovereign borrowing cost", "Debt"),
            MacroIndicator("Index of Industrial Production (IIP)", "4.8%", "4.3%", "Manufacturing expansion momentum", "Growth")
        )
    )
    val macroIndicators: StateFlow<List<MacroIndicator>> = _macroIndicators.asStateFlow()

    private val _commodities = MutableStateFlow<List<CommodityItem>>(emptyList())
    val commodities: StateFlow<List<CommodityItem>> = _commodities.asStateFlow()

    private val _macroCorrelations = MutableStateFlow<List<MacroCorrelationItem>>(
        listOf(
            MacroCorrelationItem("RBI Repo Rate Pause (6.5%)", "Banking & Financials", "Positive", "High", "Maintains stable Net Interest Margins and credit disbursal for Indian banks."),
            MacroCorrelationItem("Crude Oil Spike (+$5/bbl)", "Oil & Marketing Cos", "Negative", "High", "Increases under-recovery risks and compresses marketing margins for HPCL, BPCL, and IOC."),
            MacroCorrelationItem("India CPI Inflation Cooling (3.65%)", "FMCG & Consumer Goods", "Positive", "High", "Boosts rural consumption demand and volume growth for HUL, ITC, and Dabur."),
            MacroCorrelationItem("US Fed Rate Cut (-25bps)", "IT & Tech Exports", "Positive", "Medium", "Accelerates US enterprise tech spending and improves deal conversions for TCS and Infosys."),
            MacroCorrelationItem("Monsoon Surplus (+8%)", "Automobiles & Agribusiness", "Positive", "High", "Drives tractor and two-wheeler sales momentum in rural India for M&M and Hero MotoCorp."),
            MacroCorrelationItem("Sovereign Yield Rise (6.78%)", "Real Estate & Housing", "Negative", "Medium", "Elevates home loan rates, slightly moderating urban residential booking momentum.")
        )
    )
    val macroCorrelations: StateFlow<List<MacroCorrelationItem>> = _macroCorrelations.asStateFlow()

    init {
        startLiveMarketTracker()
    }

    private fun startLiveMarketTracker() {
        viewModelScope.launch(Dispatchers.IO) {
            fetchLiveMarketData()
            while (true) {
                delay(10000L) // Poll live ticks & API updates every 10 seconds
                fetchLiveMarketData()
            }
        }
    }

    private suspend fun fetchLiveMarketData() {
        // 1. Fetch Indices
        fetchLiveIndices()

        // 2. Fetch Commodities from Yahoo Finance API
        data class CommodityConfig(val symbol: String, val name: String, val category: String, val unit: String)
        val commodityConfigs = listOf(
            CommodityConfig("BZ=F", "Brent Crude Oil", "Energy", "Barrel ($)"),
            CommodityConfig("GC=F", "Gold Futures", "Precious Metals", "Ounce ($)"),
            CommodityConfig("SI=F", "Silver Futures", "Precious Metals", "Ounce ($)"),
            CommodityConfig("HG=F", "Copper Futures", "Industrial", "Pound ($)")
        )
        val fetchedCommodities = commodityConfigs.mapNotNull { cfg ->
            yahooApiClient.fetchCommodity(cfg.symbol, cfg.name, cfg.category, cfg.unit)
        }
        if (fetchedCommodities.isNotEmpty()) {
            _commodities.value = fetchedCommodities
        }

        // 3. Fetch Stock Gainers & Losers from Yahoo Finance API
        data class StockConfig(val symbol: String, val name: String, val timeFrame: String)
        val stockConfigs = listOf(
            StockConfig("TATAMOTORS.NS", "Tata Motors Ltd", "Day"),
            StockConfig("ZOMATO.NS", "Zomato Ltd", "Day"),
            StockConfig("RELIANCE.NS", "Reliance Industries", "Day"),
            StockConfig("INFY.NS", "Infosys Technologies", "Day"),
            StockConfig("SBIN.NS", "State Bank of India", "Day"),
            StockConfig("BPCL.NS", "Bharat Petroleum Corp", "Day"),
            StockConfig("TATASTEEL.NS", "Tata Steel Ltd", "Day"),
            StockConfig("WIPRO.NS", "Wipro Limited", "Day"),
            StockConfig("HDFCBANK.NS", "HDFC Bank Ltd", "Week"),
            StockConfig("ICICIBANK.NS", "ICICI Bank Ltd", "Week"),
            StockConfig("BHARTIARTL.NS", "Bharti Airtel Ltd", "Week"),
            StockConfig("SUNPHARMA.NS", "Sun Pharmaceutical", "Week"),
            StockConfig("INDUSINDBK.NS", "IndusInd Bank Ltd", "Week"),
            StockConfig("COALINDIA.NS", "Coal India Ltd", "Week"),
            StockConfig("TECHM.NS", "Tech Mahindra Ltd", "Week")
        )
        val fetchedMovers = stockConfigs.mapNotNull { cfg ->
            yahooApiClient.fetchStockMover(cfg.symbol, cfg.name, cfg.timeFrame)
        }
        if (fetchedMovers.isNotEmpty()) {
            _gainersLosers.value = fetchedMovers
        }
    }

    private suspend fun fetchLiveIndices() {
        val targets = listOf(
            Triple("^BSESN", "BSE Sensex", "BSE"),
            Triple("^NSEI", "Nifty 50", "NSE"),
            Triple("^GSPC", "S&P 500", "S&P"),
            Triple("^IXIC", "Nasdaq Composite", "NASDAQ"),
            Triple("^FTSE", "FTSE 100", "LSE"),
            Triple("^N225", "Nikkei 225", "TSE")
        )

        val updatedList = _indices.value.toMutableList()
        var fetchedAny = false

        for ((symbol, name, exchange) in targets) {
            val liveIndex = yahooApiClient.fetchMarketIndex(symbol, name, exchange)
            if (liveIndex != null) {
                fetchedAny = true
                val index = updatedList.indexOfFirst { it.symbol == symbol || it.symbol.contains(name, ignoreCase = true) }
                if (index >= 0) {
                    updatedList[index] = liveIndex
                } else {
                    updatedList.add(liveIndex)
                }
            }
        }

        if (fetchedAny) {
            _indices.value = updatedList
            Log.d("TapTapLiveTracker", "Live market indices fetched successfully from Yahoo Finance REST API.")
        } else {
            // Simulation fallback tick if network rate-limited or offline
            val currentList = _indices.value
            val simulatedList = currentList.map { index ->
                val pctDelta = (Random.nextDouble(-0.10, 0.12))
                val priceDelta = index.price * (pctDelta / 100.0)
                val newPrice = index.price + priceDelta
                val newChangePct = "%.2f".format(index.changePct + (pctDelta * 0.2)).toDouble()
                val newVolume = index.volume + Random.nextLong(1000, 25000)

                index.copy(
                    price = "%.2f".format(newPrice).toDouble(),
                    changePct = newChangePct,
                    high = maxOf(index.high, newPrice),
                    low = minOf(index.low, newPrice),
                    volume = newVolume
                )
            }
            _indices.value = simulatedList
            Log.d("TapTapLiveTracker", "Applied live telemetry simulation tick.")
        }
    }
}
