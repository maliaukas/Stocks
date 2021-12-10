package maliauka.sasha.yandexstocks.presentation.detail.chart

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import maliauka.sasha.yandexstocks.R
import maliauka.sasha.yandexstocks.data.util.formatPrice

class StockMarkerView(context: Context) :
    MarkerView(context, R.layout.baloon) {

    var currencyString: String = "USD"

    private val tvCost: TextView = findViewById(R.id.tv_cost_baloon)
    private val tvDate: TextView = findViewById(R.id.tv_date_baloon)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let { entry ->
            tvCost.text = formatPrice(entry.y.toDouble(), currencyString)
            tvDate.text = e.data.toString()
        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(
            (-width / 2).toFloat(),
            -height.toFloat() + 10
        )
    }
}
