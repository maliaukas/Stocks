package maliauka.sasha.yandexstocks.data.util

import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Build
import java.util.*


fun formatPrice(price: Double, currencyString: String): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault()).also {
            it.currency = Currency.getInstance(currencyString)
        }

        numberFormat.format(price)
    } else {
        price.format(2) + " " + currencyString
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)
