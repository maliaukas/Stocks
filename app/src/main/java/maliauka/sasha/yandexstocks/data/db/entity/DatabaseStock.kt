package maliauka.sasha.yandexstocks.data.db.entity

import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Build
import androidx.room.Entity
import androidx.room.PrimaryKey
import maliauka.sasha.yandexstocks.domain.model.Stock
import java.util.*

@Entity(tableName = "stocks")
data class DatabaseStock constructor(
    @PrimaryKey
    val ticker: String,
    val companyName: String,
    val currency: String,

    var price: Double,
    var delta: Double,
    var deltaPercent: Double,

    var logoUri: String? = null,
    var isFavourite: Boolean = false,

    var lastUpdateDate: String
) {
    var placeHolderColor: Int? = null
}
