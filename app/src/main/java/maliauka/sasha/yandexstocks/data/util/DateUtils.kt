package maliauka.sasha.yandexstocks.data.util

import java.text.SimpleDateFormat
import java.util.*


private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
fun getCurrentDate(): String = sdf.format(System.currentTimeMillis())
fun getDateForMillis(millis: Long): String = sdf.format(millis)
