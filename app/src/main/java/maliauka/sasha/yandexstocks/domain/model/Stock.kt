package maliauka.sasha.yandexstocks.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlin.String

@Parcelize
data class Stock(
    val ticker: String,
    val companyName: String,
    val currency: String,

    val priceString: String,
    val deltaString: String,

    val price: Double,
    val delta: Double,
    val deltaPercent: Double,

    val logoUri: String? = null,
    val isFavourite: Boolean = false,
    val placeHolderColor: Int?
): Parcelable
