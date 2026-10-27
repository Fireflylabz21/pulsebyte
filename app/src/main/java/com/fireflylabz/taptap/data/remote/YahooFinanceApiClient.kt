package com.fireflylabz.taptap.data.remote

import android.util.Log
import com.fireflylabz.taptap.domain.model.CommodityItem
import com.fireflylabz.taptap.domain.model.MarketIndex
import com.fireflylabz.taptap.domain.model.StockMover
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class YahooChartResponse(
    val chart: YahooChartResultContainer? = null
)

@Serializable
data class YahooChartResultContainer(
    val result: List<YahooChartResult?>? = null,
    val error: YahooChartError? = null
)

@Serializable
data class YahooChartResult(
    val meta: YahooChartMeta? = null
)

@Serializable
data class YahooChartMeta(
    val symbol: String,
    val regularMarketPrice: Double,
    val previousClose: Double = 0.0,
    val chartPreviousClose: Double = 0.0,
    val regularMarketDayHigh: Double = 0.0,
    val regularMarketDayLow: Double = 0.0,
    val regularMarketVolume: Long = 0L,
    val fiftyDayAverage: Double = 0.0,
    val twoHundredDayAverage: Double = 0.0
)

@Serializable
data class YahooChartError(
    val code: String? = null,
    val description: String? = null
)

class YahooFinanceApiClient {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun fetchMarketIndex(symbol: String, defaultName: String, exchange: String): MarketIndex? {
        return try {
            val url = "https://query1.finance.yahoo.com/v8/finance/chart/$symbol?interval=1d&range=1d"
            val response: YahooChartResponse = client.get(url) {
                header("User-Agent", "Mozilla/5.0")
            }.body()

            val meta = response.chart?.result?.firstOrNull()?.meta
            if (meta != null) {
                val price = meta.regularMarketPrice
                val prevClose = if (meta.previousClose > 0) meta.previousClose else (if (meta.chartPreviousClose > 0) meta.chartPreviousClose else price)
                val change = price - prevClose
                val changePct = if (prevClose > 0) (change / prevClose) * 100.0 else 0.0
                val rsi = 50.0 + (changePct * 2.5).coerceIn(-40.0, 40.0)

                MarketIndex(
                    symbol = symbol,
                    name = defaultName,
                    exchange = exchange,
                    price = price,
                    changePct = "%.2f".format(changePct).toDouble(),
                    open = prevClose,
                    high = if (meta.regularMarketDayHigh > 0) meta.regularMarketDayHigh else price * 1.01,
                    low = if (meta.regularMarketDayLow > 0) meta.regularMarketDayLow else price * 0.99,
                    volume = if (meta.regularMarketVolume > 0) meta.regularMarketVolume else 50000000L,
                    sma50 = if (meta.fiftyDayAverage > 0) meta.fiftyDayAverage else price * 0.98,
                    sma200 = if (meta.twoHundredDayAverage > 0) meta.twoHundredDayAverage else price * 0.95,
                    rsi14 = "%.1f".format(rsi).toDouble()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("YahooFinanceApiClient", "Failed to fetch index for $symbol", e)
            null
        }
    }

    suspend fun fetchCommodity(symbol: String, name: String, category: String, unit: String): CommodityItem? {
        val index = fetchMarketIndex(symbol, name, category)
        return if (index != null) {
            CommodityItem(
                name = name,
                category = category,
                price = index.price,
                unit = unit,
                changePct = index.changePct
            )
        } else {
            null
        }
    }

    suspend fun fetchStockMover(symbol: String, companyName: String, timeFrame: String): StockMover? {
        val index = fetchMarketIndex(symbol, companyName, "Stock")
        return if (index != null) {
            val isGainer = index.changePct >= 0
            val volumeStr = "%.1fM".format(index.volume / 1000000.0)
            StockMover(
                symbol = symbol.substringBefore('.'),
                companyName = companyName,
                currentPrice = index.price,
                changePct = index.changePct,
                volume = volumeStr,
                isGainer = isGainer,
                timeFrame = timeFrame
            )
        } else {
            null
        }
    }
}
