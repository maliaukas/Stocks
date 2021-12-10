package maliauka.sasha.yandexstocks.data.util

import android.graphics.drawable.Drawable
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator

private val colorGenerator: ColorGenerator = ColorGenerator.MATERIAL
fun getRandomColor(): Int = colorGenerator.randomColor

fun getLogoPlaceHolder(string: String, color: Int): Drawable {
    return TextDrawable.builder().buildRect(string, color)
}
