package maliauka.sasha.yandexstocks.presentation.detail

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import maliauka.sasha.yandexstocks.R
import maliauka.sasha.yandexstocks.data.network.dto.DatePrice
import maliauka.sasha.yandexstocks.data.util.Interval
import maliauka.sasha.yandexstocks.data.util.Result.Error
import maliauka.sasha.yandexstocks.data.util.Result.InProgress
import maliauka.sasha.yandexstocks.data.util.Result.Success
import maliauka.sasha.yandexstocks.databinding.FragmentDetailBinding
import maliauka.sasha.yandexstocks.domain.model.Stock
import maliauka.sasha.yandexstocks.presentation.detail.chart.StockMarkerView
import maliauka.sasha.yandexstocks.viewmodels.StockDetailViewModel
import retrofit2.HttpException

@AndroidEntryPoint
class StockDetailFragment : Fragment(R.layout.fragment_detail) {
    private val binding: FragmentDetailBinding by viewBinding()

    private val detailViewModel: StockDetailViewModel by viewModels()

    private fun onIntervalSelected(view: View) {
        when (view.id) {
            R.id.button_week -> {
                detailViewModel.fetchChartData(Interval.WEEK)
            }
            R.id.button_month -> {
                detailViewModel.fetchChartData(Interval.MONTH)
            }
            R.id.button_six_month -> {
                detailViewModel.fetchChartData(Interval.SIX_MONTHS)
            }
            R.id.button_year -> {
                detailViewModel.fetchChartData(Interval.YEAR)
            }
            R.id.button_all -> {
                detailViewModel.fetchChartData(Interval.ALL)
            }
        }
    }

    private fun highlightButton(button: Button) {
        binding.buttonWeek.backgroundTintList = ColorStateList.valueOf(generalColor)
        binding.buttonWeek.setTextColor(onGeneralColor)

        binding.buttonMonth.backgroundTintList = ColorStateList.valueOf(generalColor)
        binding.buttonMonth.setTextColor(onGeneralColor)

        binding.buttonSixMonth.backgroundTintList = ColorStateList.valueOf(generalColor)
        binding.buttonSixMonth.setTextColor(onGeneralColor)

        binding.buttonYear.backgroundTintList = ColorStateList.valueOf(generalColor)
        binding.buttonYear.setTextColor(onGeneralColor)

        binding.buttonAll.backgroundTintList = ColorStateList.valueOf(generalColor)
        binding.buttonAll.setTextColor(onGeneralColor)

        button.backgroundTintList = ColorStateList.valueOf(selectedColor)
        button.setTextColor(onSelectedColor)
    }

    private val redColor: Int by lazy {
        val typedValue = TypedValue()
        val theme = requireActivity().theme

        theme.resolveAttribute(R.attr.colorError, typedValue, true)
        ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)
    }

    private val greenColor: Int by lazy {
        val typedValue = TypedValue()
        val theme = requireActivity().theme

        theme.resolveAttribute(R.attr.colorPositive, typedValue, true)
        ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)
    }

    private val selectedColor: Int by lazy {
        val typedValue = TypedValue()
        val theme = requireActivity().theme

        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)
    }

    private val onSelectedColor: Int by lazy {
        val typedValue = TypedValue()
        val theme = requireActivity().theme

        theme.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
        ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)
    }

    private val generalColor: Int by lazy {
        val typedValue = TypedValue()
        val theme = requireActivity().theme

        theme.resolveAttribute(R.attr.colorSurface, typedValue, true)
        ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)
    }

    private val onGeneralColor: Int by lazy {
        val typedValue = TypedValue()
        val theme = requireActivity().theme

        theme.resolveAttribute(R.attr.colorOnSurface, typedValue, true)
        ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupOnClickListeners()
        setupChart()

        onIntervalSelected(binding.buttonMonth)
    }

    private fun setupChart() {
        with(binding.chart) {
            visibility = View.INVISIBLE
            legend.isEnabled = false
            description.isEnabled = false

            setExtraOffsets(50f, 100f, 50f, 0f)

            setDrawGridBackground(false)
            setDrawBorders(false)
            setNoDataTextColor(selectedColor)

            setScaleEnabled(false)
            setPinchZoom(false)

            isDoubleTapToZoomEnabled = false

            with(xAxis) {
                setDrawAxisLine(false)
                setDrawGridLines(false)
                setDrawLabels(false)
            }

            with(axisLeft) {
                setDrawAxisLine(false)
                setDrawGridLines(false)
                setDrawLabels(false)
            }

            with(axisRight) {
                setDrawAxisLine(false)
                setDrawGridLines(false)
                setDrawLabels(false)
            }
        }
    }

    private fun setupOnClickListeners() {
        binding.ibBack.setOnClickListener {
            findNavController().navigate(StockDetailFragmentDirections.actionStockDetailFragmentToMainFragment())
        }

        binding.buttonAll.setOnClickListener(this::onIntervalSelected)
        binding.buttonWeek.setOnClickListener(this::onIntervalSelected)
        binding.buttonMonth.setOnClickListener(this::onIntervalSelected)
        binding.buttonSixMonth.setOnClickListener(this::onIntervalSelected)
        binding.buttonYear.setOnClickListener(this::onIntervalSelected)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailViewModel.historicalData.collectLatest { result ->
                    when (result) {
                        is Success -> {
                            drawChart(result.data)
                            binding.progressBar.isVisible = false
                        }
                        InProgress -> {
                            binding.progressBar.isVisible = true
                        }
                        is Error -> {
                            Log.e(
                                "SHAS",
                                "aa  = " + result.exception.message + "\n" + result.exception.toString() + "\n" +
                                        result.exception.cause.toString()
                            )

                            binding.progressBar.isVisible = false

                            val errorText =
                                if (result.exception is HttpException) {
                                    getString(R.string.api_calls_message)
                                } else "An error occured!"

                            Snackbar.make(binding.chart, errorText, Snackbar.LENGTH_SHORT)
                                .show()

                            drawChart(emptyList())
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailViewModel.selectedStock.collectLatest { stock ->
                    setupView(stock)

                    val marker = StockMarkerView(requireContext()).apply {
                        currencyString = stock.currency
                    }
                    binding.chart.marker = marker
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailViewModel.interval.collectLatest {
                    when (it) {
                        Interval.ALL -> highlightButton(binding.buttonAll)
                        Interval.WEEK -> highlightButton(binding.buttonWeek)
                        Interval.MONTH -> highlightButton(binding.buttonMonth)
                        Interval.SIX_MONTHS -> highlightButton(binding.buttonSixMonth)
                        Interval.YEAR -> highlightButton(binding.buttonYear)
                    }
                }
            }
        }
    }

    private fun drawChart(list: List<DatePrice>) {
        binding.chart.visibility = View.VISIBLE

        if (list.isEmpty()) {
            binding.chart.notifyDataSetChanged()
            return
        }

        val entries = list.mapIndexed { i, item ->
            Entry((i + 1).toFloat(), item.close.toFloat()).apply {
                this.data = item.date
            }
        }

        val dataSet = LineDataSet(entries, "").apply {
            setDrawHighlightIndicators(false)
            setDrawFilled(true)
            fillDrawable =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.gradient_background,
                    null
                )

            setDrawCircles(false)
            setDrawValues(false)

            color = selectedColor
            lineWidth = 2.5f
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        binding.chart.data = LineData(dataSet)
        binding.chart.invalidate()
    }

    private fun setupView(stock: Stock) {
        binding.tvTickerCompany.text = stock.ticker + "\n" + stock.companyName

        binding.tvCost.text = stock.priceString

        binding.tvDelta.text = stock.deltaString
        binding.tvDelta.setTextColor(if (stock.delta > 0.0) greenColor else redColor)

        binding.ibFavourite.setImageResource(
            if (stock.isFavourite) R.drawable.ic_star_outline_filled
            else R.drawable.ic_star_outline
        )

        binding.ibFavourite.setOnClickListener {
            detailViewModel.onFavButtonClick(stock)
        }
    }
}
