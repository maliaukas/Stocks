package maliauka.sasha.yandexstocks.presentation.list.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import maliauka.sasha.yandexstocks.data.repository.StocksRepository
import maliauka.sasha.yandexstocks.data.util.Result
import maliauka.sasha.yandexstocks.domain.model.Stock
import javax.inject.Inject

@HiltViewModel
class StocksViewModel @Inject constructor(
    override val stocksRepository: StocksRepository
) : BaseListViewModel() {

    override val stocks: Flow<List<Stock>>
        get() = stocksRepository.stocks

//    val showErrorMessage = stocksRepository.showStocksError
//
//    val showProgressBar= stocksRepository.showStocksLoading
}
