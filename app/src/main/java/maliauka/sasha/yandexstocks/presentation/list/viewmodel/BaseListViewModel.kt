package maliauka.sasha.yandexstocks.presentation.list.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import maliauka.sasha.yandexstocks.domain.model.Stock
import maliauka.sasha.yandexstocks.data.repository.StocksRepository
import maliauka.sasha.yandexstocks.data.util.Result
import retrofit2.HttpException
import javax.inject.Inject

abstract class BaseListViewModel : ViewModel() {

//    init {
//        Log.d("SHAS", "init base viewmodel")
//        viewModelScope.launch {
//
//            stocks.collectLatest { result ->
//                when (result) {
//                    is Result.Success -> {
//                        onSuccess(result.data)
//                    }
//
//                    is Result.Error -> {
//                        onError(result.exception)
//                    }
//
//                    Result.InProgress -> {
//                        onInProgress()
//                    }
//                }
//            }
//        }
//    }


    abstract val stocksRepository: StocksRepository

    abstract val stocks: Flow<List<Stock>>

//    abstract val showErrorMessage: Flow<Pair<Boolean, String>>
//
//    abstract val showProgressBar: Flow<Boolean>

//    private val _stocksList = MutableSharedFlow<List<Stock>>()
//    val stocksList: SharedFlow<List<Stock>>
//        get() = _stocksList.asSharedFlow()

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

//    private val _showProgressBar = MutableStateFlow(false)
//    val showProgressBar: StateFlow<Boolean>
//        get() = _showProgressBar.asStateFlow()


//    private val _showErrorMessage = MutableStateFlow(false)
//    val showErrorMessage: StateFlow<Boolean>
//        get() = _showErrorMessage.asStateFlow()

//    private fun onSuccess(data: Flow<List<Stock>>) {
//        Log.d("SHAS", "success")
//
//        hideAllMessagesAndProgressBar()
//
//        viewModelScope.launch {
//            data.collectLatest {
//                _stocksList.emit(it)
//            }
//        }
//    }


//    private fun onInProgress() {
//        Log.d("SHAS", "in progress")
//       // showOnlyProgressBar()
//    }
//
//    private fun onError(e: Exception) {
//        Log.e("SHAS", e.message.toString() + "\n" + e.toString())
//
//
//       // showOnlyErrorMessage()
//    }
//
//    private fun hideAllMessagesAndProgressBar() {
//        _showProgressBar.value = false
//        _showErrorMessage.value = false
//    }
//
//    private fun showOnlyProgressBar() {
//        _showProgressBar.value = true
//        _showErrorMessage.value = false
//
//    }
//
//    private fun showOnlyErrorMessage() {
//        _showErrorMessage.value = true
//
//        _showProgressBar.value = false
//    }
}
