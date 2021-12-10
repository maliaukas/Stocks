package maliauka.sasha.yandexstocks.data.network.dto

import com.squareup.moshi.Json

data class SearchResponse(
    @Json(name = "symbol")
    val ticker: String
)
