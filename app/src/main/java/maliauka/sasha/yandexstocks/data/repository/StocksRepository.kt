package maliauka.sasha.yandexstocks.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import maliauka.sasha.yandexstocks.data.db.entity.extension.asDomainModel
import maliauka.sasha.yandexstocks.data.network.FmpApi
import maliauka.sasha.yandexstocks.data.network.dto.DatePrice
import maliauka.sasha.yandexstocks.data.network.dto.StockResponse
import maliauka.sasha.yandexstocks.data.network.dto.TrendingTickerResponse
import maliauka.sasha.yandexstocks.data.network.dto.extension.asDatabaseModel
import maliauka.sasha.yandexstocks.data.util.Interval
import maliauka.sasha.yandexstocks.data.util.Result
import maliauka.sasha.yandexstocks.data.util.getCurrentDate
import maliauka.sasha.yandexstocks.data.util.getRandomColor
import maliauka.sasha.yandexstocks.db.StocksDatabase
import maliauka.sasha.yandexstocks.domain.model.Stock
import javax.inject.Inject


class StocksRepository @Inject constructor(private val database: StocksDatabase) {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String>
        get() = _query.asStateFlow()

    private val _searchResult =
        MutableStateFlow<Result<Flow<List<Stock>>>>(Result.InProgress)
    val searchResult: StateFlow<Result<Flow<List<Stock>>>>
        get() = _searchResult.asStateFlow()

    val stocks = database.stockDao.getStocksFlow().map {
        it.asDomainModel()
    }

    val favStocks: Flow<List<Stock>> = database.stockDao.getFavouriteStocksFlow().map {
        it.asDomainModel()
    }

    suspend fun fetchTrendingTickers() {
        Log.d("SHAS", "fetchTrendingStocks is called")

        val dbRecordsCount = database.stockDao.getRecordsCount()
        if (dbRecordsCount != 0) {
            return
        }

        try {
            withContext(Dispatchers.IO) {
                val trendingResponseList: List<TrendingTickerResponse> =
                    FmpApi.retrofitService.getMostActiveTickers()

                val stockResponses = getStocksForTrendingTickers(trendingResponseList)

                database.stockDao.insertAll(stockResponses.asDatabaseModel())
            }
        } catch (e: Exception) {
            Log.e("SHAS", "err:" + e.message)
        } finally {
            Log.d("SHAS", "finally fetchTrendingTickers")
        }
    }

    private suspend fun getStocksForTrendingTickers(tickers: List<TrendingTickerResponse>): List<StockResponse> {
        return tickers.map { ticker: TrendingTickerResponse ->
            FmpApi.retrofitService.getStock(ticker.ticker).first()
                .apply { deltaPercent = ticker.deltaPercent }
        }
    }

    private suspend fun getStocksForTickers(tickers: List<String>): List<StockResponse> {
        return tickers.mapNotNull { ticker ->
            try {
                FmpApi.retrofitService.getStock(ticker).first().apply {
                    deltaPercent =
                        FmpApi.retrofitService.updateStockData(ticker).first().changesPercentage
                }
            } // can catch exception for stocks that are not available on free API plan
            // skip such stocks
            catch (e: Exception) {
                null
            }
        }
    }

    // once a day
    suspend fun refreshStocks() {
        try {
            withContext(Dispatchers.IO) {
                Log.d("SHAS", "refresh stocks is called")

                val date = getCurrentDate()
                val stocksToUpdate = database.stockDao.getStocksToUpdate(date)


                stocksToUpdate.forEach { stock ->
                    val stockUpdateResponse =
                        FmpApi.retrofitService.updateStockData(stock.ticker).first()

                    database.stockDao.updateStockPrice(
                        stock.ticker,
                        stockUpdateResponse.price,
                        stockUpdateResponse.change,
                        stockUpdateResponse.changesPercentage,
                        date
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("SHAS", "error while updating: ${e.message}")
        }
    }

    private val _chartData =
        MutableStateFlow<Result<List<DatePrice>>>(Result.InProgress)

    val chartData: StateFlow<Result<List<DatePrice>>>
        get() = _chartData.asStateFlow()

    suspend fun fetchChartHistoricalData(
        ticker: String,
        interval: Interval
    ) {
        _chartData.emit(Result.InProgress)

        try {
            val responseChartData = if (interval == Interval.ALL) {
                FmpApi.retrofitService.getAllHistoricalData(
                    ticker = ticker
                ).historical.reversed()
            } else {
                FmpApi.retrofitService.getHistoricalDataForInterval(
                    ticker = ticker,
                    from = interval.getFromDate(),
                    to = interval.getToDate()
                ).historical.reversed()
            }

            _chartData.emit(Result.Success(responseChartData))
        } catch (e: Exception) {
            _chartData.emit(Result.Error(e))
        }
    }

    suspend fun toggleFavourite(stock: Stock) {
        withContext(Dispatchers.IO) {
            if (stock.isFavourite) {
                database.stockDao.unFavourite(stock.ticker)
            } else {
                database.stockDao.makeFavourite(stock.ticker)
            }
        }
    }

    suspend fun search(newQuery: String) {
        if (query.value == newQuery)
            return

        _query.value = newQuery

        try {
            withContext(Dispatchers.IO) {
                _searchResult.emit(Result.InProgress)

                val foundTickers = FmpApi.retrofitService.search(newQuery).map { it.ticker }

                val cachedStocks = database.stockDao.getStocksForTickers(foundTickers)

                val cachedTickers = cachedStocks.map { it.ticker }

                val notCachedTickers = foundTickers.filter {
                    !cachedTickers.contains(it)
                }

                val notCachedStocks = getStocksForTickers(notCachedTickers)

                database.stockDao.insertAll(notCachedStocks.asDatabaseModel())
            }
        } catch (e: Exception) {
            _searchResult.emit(Result.Error(e))
        } finally {
            Log.d("SHAS", "search finally")

            val a = database.stockDao.search("%${newQuery}%").map { it.asDomainModel() }
            _searchResult.emit(Result.Success(a))
        }
    }

    suspend fun setStockColorPlaceHolder(stock: Stock) {
        withContext(Dispatchers.IO) {
            database.stockDao.setStockColorPlaceHolder(stock.ticker, getRandomColor())
        }
    }

    fun getStock(ticker: String): Flow<Stock> {
        return database.stockDao.getStock(ticker).map { it.asDomainModel() }
    }
}
