package com.fireflylabz.pulsebyte.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MarketIndex(
    val symbol: String,
    val name: String,
    val exchange: String,
    val price: Double,
    val changePct: Double,
    val open: Double,
    val high: Double,
    val low: Double,
    val volume: Long,
    val sma50: Double,
    val sma200: Double,
    val rsi14: Double
)

@Serializable
data class StockScreenerItem(
    val rank: Int,
    val symbol: String,
    val name: String,
    val marketCapCategory: String, // LargeCap, MidCap, SmallCap
    val marketCapUsd: Double,
    val monthlyReturnPct: Double,
    val volumeSpikeFactor: Double,
    val sharpeRatio: Double
)

@Serializable
data class MutualFundItem(
    val rank: Int,
    val fundName: String,
    val category: String,
    val nav: Double,
    val monthlyReturnPct: Double,
    val threeYrCagrPct: Double,
    val expenseRatioPct: Double
)

@Serializable
data class MacroIndicator(
    val title: String,
    val value: String,
    val previousValue: String,
    val changeNote: String,
    val category: String // Central Bank, Inflation, Employment, Debt
)

@Serializable
data class CommodityItem(
    val name: String,
    val category: String, // Energy, Precious Metals, Industrial, Agricultural
    val price: Double,
    val unit: String,
    val changePct: Double
)

@Serializable
data class FinancialTip(
    val title: String,
    val category: String,
    val description: String,
    val tacticalAction: String
)
