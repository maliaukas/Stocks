package maliauka.sasha.yandexstocks.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import maliauka.sasha.yandexstocks.data.util.Interval
import maliauka.sasha.yandexstocks.domain.model.Stock
import maliauka.sasha.yandexstocks.data.repository.StocksRepository
import javax.inject.Inject

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val stocksRepository: StocksRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val ticker = savedStateHandle.get<Stock>("stock")!!.ticker

    val selectedStock = stocksRepository.getStock(ticker)

    private val _interval = MutableStateFlow(Interval.MONTH)
    val interval: StateFlow<Interval>
        get() = _interval.asStateFlow()

    fun fetchChartData(interval: Interval) {
        viewModelScope.launch {
            _interval.emit(interval)
            stocksRepository.fetchChartHistoricalData(ticker, interval)
        }
    }

    fun onFavButtonClick(stock: Stock) {
        viewModelScope.launch {
            stocksRepository.toggleFavourite(stock)
        }
    }

    val historicalData = stocksRepository.chartData
}
