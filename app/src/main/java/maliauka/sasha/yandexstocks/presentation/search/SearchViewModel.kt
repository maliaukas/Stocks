package maliauka.sasha.yandexstocks.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import maliauka.sasha.yandexstocks.data.util.Result
import maliauka.sasha.yandexstocks.domain.model.Stock
import maliauka.sasha.yandexstocks.data.repository.StocksRepository
import maliauka.sasha.yandexstocks.presentation.list.viewmodel.BaseListViewModel
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(override val stocksRepository: StocksRepository) :
    BaseListViewModel() {

    private val _showResultList = MutableStateFlow(false)
    val showResultList: StateFlow<Boolean>
        get() = _showResultList.asStateFlow()

    private val _showProgressBar = MutableStateFlow(false)
    val showProgressBar: StateFlow<Boolean>
        get() = _showProgressBar.asStateFlow()


    private val _showErrorMessage = MutableStateFlow(false to "")
    val showErrorMessage: StateFlow<Pair<Boolean, String>>
        get() = _showErrorMessage.asStateFlow()

    private val _showEmptyMessage = MutableStateFlow(false)
    val showEmptyMessage: StateFlow<Boolean>
        get() = _showEmptyMessage.asStateFlow()

    private val _showApiCallsMessage = MutableStateFlow(false)
    val showApiCallsMessage: StateFlow<Boolean>
        get() = _showApiCallsMessage.asStateFlow()


    fun setShowResultList(show: Boolean) {
        _showResultList.value = show
    }

    val query = stocksRepository.query

    private val _searchResult = MutableSharedFlow<List<Stock>>()
    val searchResult: SharedFlow<List<Stock>>
        get() = _searchResult.asSharedFlow()

    fun search() {
        search(query.value)
    }

    private var searchJob: Job? = null
    private var collectJob: Job? = null

    fun search(newQuery: String) {
        if (query.value != newQuery) {
            searchJob?.cancel()
            searchJob = viewModelScope.launch {
                stocksRepository.search(newQuery)
            }

            viewModelScope.launch {
                stocksRepository.searchResult.collectLatest { result ->
                    when (result) {
                        is Result.Success -> {
                            onSuccess(result.data)
                        }
                        is Result.Error -> {
                            onError(result.exception)
                        }
                        Result.InProgress -> {
                            onInProgress()
                        }
                    }
                }
            }
        }
    }

    private fun onSuccess(data: Flow<List<Stock>>) {
        hideAllMessagesAndProgressBar()

        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            data.collectLatest {
                _searchResult.emit(it)
                _showEmptyMessage.emit(it.isEmpty())
            }
        }
    }

    private fun onInProgress() {
        showOnlyProgressBar()
    }

    private fun onError(e: Exception) {
        Log.e("SHAS", e.message.toString() + "\n" + e.toString())

        when (e) {
            is HttpException -> {
                showOnlyApiCallsMessage()
            }
            is CancellationException -> {
                // ignore
            }
            else -> {
                showOnlyErrorMessage(e.message.toString())
            }
        }
    }

    private fun hideAllMessagesAndProgressBar() {
        _showProgressBar.value = false
        _showErrorMessage.value = false to ""
        _showApiCallsMessage.value = false
    }

    private fun showOnlyProgressBar() {
        _showProgressBar.value = true

        _showErrorMessage.value = false to ""
        _showApiCallsMessage.value = false
    }

    private fun showOnlyApiCallsMessage() {
        _showApiCallsMessage.value = true

        _showErrorMessage.value = false to ""
        _showProgressBar.value = false
    }

    private fun showOnlyErrorMessage(message: String) {
        _showErrorMessage.value = true to message

        _showProgressBar.value = false
        _showApiCallsMessage.value = false
    }

    // is not used
    override val stocks: Flow<List<Stock>>
        get() = emptyFlow()
}

