package com.fireflylabz.taptap.domain.model

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
data class StockMover(
    val symbol: String,
    val companyName: String,
    val currentPrice: Double,
    val changePct: Double,
    val volume: String,
    val isGainer: Boolean,
    val timeFrame: String // "Day" or "Week"
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
    val category: String
)

@Serializable
data class CommodityItem(
    val name: String,
    val category: String,
    val price: Double,
    val unit: String,
    val changePct: Double
)

@Serializable
data class MacroCorrelationItem(
    val triggerEvent: String,
    val affectedSector: String,
    val impactType: String, // Positive, Negative, Neutral
    val impactMagnitude: String, // High, Medium, Low
    val explanation: String
)

@Serializable
data class MarketAlertItem(
    val alertId: String,
    val title: String,
    val severity: String, // HIGH, MEDIUM, INFO
    val timestamp: String,
    val triggerReason: String,
    val recommendation: String
)

@Serializable
data class IpoNewsItem(
    val id: String,
    val companyName: String,
    val issueSize: String,
    val priceBand: String,
    val openingDate: String,
    val closingDate: String,
    val description: String,
    val status: String // Open, Upcoming, Listed
)
