package maliauka.sasha.yandexstocks.data.db.entity.extension

import maliauka.sasha.yandexstocks.data.db.entity.DatabaseStock
import maliauka.sasha.yandexstocks.data.util.format
import maliauka.sasha.yandexstocks.data.util.formatPrice
import maliauka.sasha.yandexstocks.domain.model.Stock
import java.util.*

fun List<DatabaseStock>.asDomainModel(): List<Stock> {
    return map { it.asDomainModel() }
}

fun DatabaseStock.asDomainModel(): Stock {
    return Stock(
        ticker = this.ticker,
        companyName = this.companyName,
        currency = this.currency,
        price = this.price,
        delta = this.delta,
        deltaPercent = this.deltaPercent,
        logoUri = this.logoUri,
        isFavourite = this.isFavourite,
        priceString = formatPrice(this.price, this.currency),
        deltaString = formatDeltaPrice(this.delta, this.deltaPercent, this.currency),
        placeHolderColor = this.placeHolderColor
    )
}

private fun formatDeltaPrice(delta: Double, deltaPercent: Double, currencyString: String): String {
    val deltaFormatted = formatPrice(delta, currencyString)

    val plus = (if (delta > 0) "+" else "")

    return plus + deltaFormatted + " (" + plus + deltaPercent.format(2) + "%)"
}
