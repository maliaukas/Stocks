package maliauka.sasha.yandexstocks.presentation.list.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import maliauka.sasha.yandexstocks.data.repository.StocksRepository
import maliauka.sasha.yandexstocks.domain.model.Stock

abstract class BaseListViewModel : ViewModel() {

    abstract val stocksRepository: StocksRepository

    abstract val stocks: Flow<List<Stock>>

    fun onFavButtonClick(stock: Stock) {
        viewModelScope.launch {
            stocksRepository.toggleFavourite(stock)
        }
    }

    fun setStockColorPlaceHolder(stock: Stock) {
        viewModelScope.launch {
            stocksRepository.setStockColorPlaceHolder(stock)
        }
    }

    private val _navigateToSelectedStock = MutableStateFlow<Stock?>(null)
    val navigateToSelectedStock: StateFlow<Stock?>
        get() = _navigateToSelectedStock.asStateFlow()

    fun displayStockDetails(stock: Stock?) {
        _navigateToSelectedStock.value = stock
    }

    fun displayStockDetailsComplete() {
        _navigateToSelectedStock.value = null
    }
}
