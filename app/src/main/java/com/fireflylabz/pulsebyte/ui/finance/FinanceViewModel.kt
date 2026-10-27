package com.fireflylabz.pulsebyte.ui.finance

import androidx.lifecycle.ViewModel
import com.fireflylabz.pulsebyte.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FinanceViewModel : ViewModel() {

    private val _indices = MutableStateFlow<List<MarketIndex>>(
        listOf(
            MarketIndex("BSE:SENSEX", "BSE Sensex", "BSE", 80410.75, 1.25, 80120.50, 80550.25, 79980.10, 42050000, 79200.30, 76500.10, 62.4),
            MarketIndex("NSE:NIFTY50", "Nifty 50", "NSE", 24530.20, 1.10, 24300.00, 24580.50, 24250.00, 89050000, 24100.50, 23200.00, 58.9),
            MarketIndex("US:SPX", "S&P 500", "NYSE", 5870.40, 0.65, 5840.10, 5885.20, 5835.00, 310000000, 5780.00, 5500.00, 64.2),
            MarketIndex("US:IXIC", "Nasdaq Composite", "NASDAQ", 18712.50, 0.85, 18580.00, 18750.00, 18550.00, 450000000, 18300.00, 17200.00, 66.5),
            MarketIndex("UK:FTSE", "FTSE 100", "LSE", 8240.10, -0.20, 8260.00, 8280.00, 8210.00, 120000000, 8200.00, 7900.00, 49.1),
            MarketIndex("JP:N225", "Nikkei 225", "TSE", 39120.00, 1.45, 38600.00, 39250.00, 38550.00, 98000000, 38400.00, 36800.00, 61.8)
        )
    )
    val indices: StateFlow<List<MarketIndex>> = _indices.asStateFlow()

    private val _stockScreener = MutableStateFlow<List<StockScreenerItem>>(
        listOf(
            StockScreenerItem(1, "TECH_SMALL", "Apex Clean Energy Tech", "SmallCap", 1200000000.0, 28.4, 3.2, 2.15),
            StockScreenerItem(2, "FIN_MID", "Vanguard Digital Bank", "MidCap", 7500000000.0, 22.1, 2.8, 1.90),
            StockScreenerItem(3, "AI_LARGE", "Quantum Semiconductors", "LargeCap", 125000000000.0, 18.7, 2.4, 2.40),
            StockScreenerItem(4, "RENEW_MID", "EcoGrid Solar Corp", "MidCap", 4200000000.0, 17.5, 2.1, 1.75),
            StockScreenerItem(5, "BIO_SMALL", "Genomics Therapeutics", "SmallCap", 850000000.0, 32.0, 4.1, 2.30)
        )
    )
    val stockScreener: StateFlow<List<StockScreenerItem>> = _stockScreener.asStateFlow()

    private val _mutualFunds = MutableStateFlow<List<MutualFundItem>>(
        listOf(
            MutualFundItem(1, "Axis Small Cap Fund", "Equity: Small Cap", 98.40, 8.7, 22.3, 0.65),
            MutualFundItem(2, "Mirae Asset Large Cap", "Equity: Large Cap", 112.50, 5.2, 17.8, 0.52),
            MutualFundItem(3, "SBI Bluechip Fund", "Equity: Large Cap", 84.20, 4.8, 16.5, 0.70),
            MutualFundItem(4, "ICICI Pru Technology", "Sectoral: Tech", 145.10, 9.4, 24.1, 0.78)
        )
    )
    val mutualFunds: StateFlow<List<MutualFundItem>> = _mutualFunds.asStateFlow()

    private val _macroIndicators = MutableStateFlow<List<MacroIndicator>>(
        listOf(
            MacroIndicator("US Fed Funds Rate", "5.25%", "5.50%", "-25 bps rate cut cycle", "Central Bank"),
            MacroIndicator("RBI Repo Rate", "6.50%", "6.50%", "Paused for monetary stability", "Central Bank"),
            MacroIndicator("US CPI Inflation", "2.4%", "2.7%", "Cooling toward 2% target", "Inflation"),
            MacroIndicator("India CPI Inflation", "3.65%", "4.10%", "Food price normalization", "Inflation"),
            MacroIndicator("Non-Farm Payrolls", "254K", "142K", "Strong labor market expansion", "Employment"),
            MacroIndicator("US 10Y Treasury Yield", "4.21%", "3.95%", "+26 bps term premium", "Debt")
        )
    )
    val macroIndicators: StateFlow<List<MacroIndicator>> = _macroIndicators.asStateFlow()

    private val _commodities = MutableStateFlow<List<CommodityItem>>(
        listOf(
            CommodityItem("Brent Crude Oil", "Energy", 74.50, "Barrel ($)", -1.20),
            CommodityItem("WTI Crude Oil", "Energy", 70.80, "Barrel ($)", -1.10),
            CommodityItem("Gold Spot", "Precious Metals", 2740.20, "Ounce ($)", 0.85),
            CommodityItem("Silver Spot", "Precious Metals", 33.80, "Ounce ($)", 1.40),
            CommodityItem("Copper Futures", "Industrial", 4.35, "Pound ($)", 0.45),
            CommodityItem("Natural Gas", "Energy", 2.85, "MMBtu ($)", -2.30)
        )
    )
    val commodities: StateFlow<List<CommodityItem>> = _commodities.asStateFlow()

    private val _financialTips = MutableStateFlow<List<FinancialTip>>(
        listOf(
            FinancialTip(
                "Tactical Asset Allocation in High VIX",
                "Strategy",
                "During high volatility (VIX > 20), rebalance core equity weightings to defensive sectors (FMCG, Utilities) and allocate 10% to short-term sovereign debt.",
                "Review Portfolio Beta"
            ),
            FinancialTip(
                "Dollar-Cost Averaging (DCA) Discipline",
                "Financial Literacy",
                "Avoid lump-sum execution during macroeconomic inflection points. Systematic monthly deployment mitigates drawdown risk by 34%.",
                "Set Up SIP Rule"
            ),
            FinancialTip(
                "Discounted Cash Flow (DCF) Margin of Safety",
                "Valuation",
                "Always demand a 20% to 25% margin of safety below intrinsic DCF valuation before acquiring cyclical or small-cap equities.",
                "Open DCF Calculator"
            )
        )
    )
    val financialTips: StateFlow<List<FinancialTip>> = _financialTips.asStateFlow()

    // Calculator states
    fun calculateDcf(fcf: Double, growthRate: Double, discountRate: Double, terminalGrowth: Double, shares: Double): Double {
        if (shares <= 0) return 0.0
        // Simplified 5-year DCF model
        var presentValueSum = 0.0
        var currentFcf = fcf
        val g = growthRate / 100.0
        val r = discountRate / 100.0
        val tg = terminalGrowth / 100.0

        for (year in 1..5) {
            currentFcf *= (1.0 + g)
            presentValueSum += currentFcf / Math.pow(1.0 + r, year.toDouble())
        }

        val terminalValue = (currentFcf * (1.0 + tg)) / (r - tg)
        val discountedTerminalValue = terminalValue / Math.pow(1.0 + r, 5.0)

        val enterpriseValue = presentValueSum + discountedTerminalValue
        return enterpriseValue / shares
    }

    fun calculatePeg(peRatio: Double, growthRatePct: Double): Double {
        if (growthRatePct <= 0) return 0.0
        return peRatio / growthRatePct
    }

    fun calculateRoe(netIncome: Double, shareholdersEquity: Double): Double {
        if (shareholdersEquity <= 0) return 0.0
        return (netIncome / shareholdersEquity) * 100.0
    }
}
