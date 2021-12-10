package maliauka.sasha.yandexstocks.data.network.dto

import com.squareup.moshi.Json

data class StockResponse(

    // all the fields below are retrieved by
    // https://site.financialmodelingprep.com/developer/docs#Company-Outlook
    @Json(name = "symbol")
    val ticker: String?,

    val companyName: String?,

    val price: Double?,

    @Json(name = "changes")
    val delta: Double?,

    val currency: String?,

    @Json(name = "image")
    val logoUri: String?,

    // this field is retrieved from TrendingTickerResponse
   // @Json(name = "changesPercentage")
    var deltaPercent: Double? = null
) {
    var isFavourite: Boolean = false
}
