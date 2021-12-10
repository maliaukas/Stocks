package maliauka.sasha.yandexstocks.data.network.dto.extension

import maliauka.sasha.yandexstocks.data.db.entity.DatabaseStock
import maliauka.sasha.yandexstocks.data.network.dto.StockResponse
import maliauka.sasha.yandexstocks.data.util.getCurrentDate
import maliauka.sasha.yandexstocks.data.util.getRandomColor


fun List<StockResponse>.asDatabaseModel(): List<DatabaseStock> {
    return mapNotNull { response: StockResponse ->
        if (response.companyName == null ||
            response.currency == null ||
            response.delta == null ||
            response.ticker == null ||
            response.price == null ||
            response.deltaPercent == null
        ) null
        else
            DatabaseStock(
                ticker = response.ticker,
                companyName = response.companyName,
                currency = response.currency,
                price = response.price,
                delta = response.delta,
                deltaPercent = response.deltaPercent!!,
                logoUri = response.logoUri?.trim(),
                isFavourite = response.isFavourite,
                lastUpdateDate = getCurrentDate()
            ).also {
                if (response.logoUri == null)
                    it.placeHolderColor = getRandomColor()
            }
    }
}

