package maliauka.sasha.yandexstocks.data.network.dto

data class StockUpdateResponse(
    val price: Double,
    val change: Double,
    val changesPercentage: Double
)
