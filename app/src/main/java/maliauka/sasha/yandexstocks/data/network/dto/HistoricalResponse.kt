package maliauka.sasha.yandexstocks.data.network.dto

data class HistoricalResponse(
    val symbol: String,
    val historical: List<DatePrice>
)
