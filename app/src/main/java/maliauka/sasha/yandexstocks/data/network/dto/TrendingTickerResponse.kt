package maliauka.sasha.yandexstocks.data.network.dto

import com.squareup.moshi.Json

data class TrendingTickerResponse(
    val ticker: String,

    @Json(name = "changesPercentage")
    val deltaPercent: Double
)
