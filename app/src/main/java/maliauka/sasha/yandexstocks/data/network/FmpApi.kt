package maliauka.sasha.yandexstocks.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import maliauka.sasha.yandexstocks.BuildConfig
import maliauka.sasha.yandexstocks.data.network.dto.HistoricalResponse
import maliauka.sasha.yandexstocks.data.network.dto.SearchResponse
import maliauka.sasha.yandexstocks.data.network.dto.StockResponse
import maliauka.sasha.yandexstocks.data.network.dto.StockUpdateResponse
import maliauka.sasha.yandexstocks.data.network.dto.TrendingTickerResponse
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val FMP_BASE_URL = "https://financialmodelingprep.com/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(FMP_BASE_URL)
    .build()

interface FmpApiService {
    @GET("api/v3/actives")
    suspend fun getMostActiveTickers(
        @Query("apikey") apiKey: String = FmpApi.key
    ): List<TrendingTickerResponse>

    @GET("api/v3/profile/{symbol}")
    suspend fun getStock(
        @Path("symbol") ticker: String,
        @Query("apikey") apiKey: String = FmpApi.key
    ): List<StockResponse>

    @GET("api/v3/quote/{symbol}")
    suspend fun updateStockData(
        @Path("symbol") ticker: String,
        @Query("apikey") apiKey: String = FmpApi.key
    ): List<StockUpdateResponse>

    @GET("api/v3/historical-price-full/{symbol}")
    suspend fun getHistoricalDataForInterval(
        @Path("symbol") ticker: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("serietype") type: String = "line",
        @Query("apikey") apiKey: String = FmpApi.key
    ): HistoricalResponse

    @GET("api/v3/historical-price-full/{symbol}")
    suspend fun getAllHistoricalData(
        @Path("symbol") ticker: String,
        @Query("serietype") type: String = "line",
        @Query("apikey") apiKey: String = FmpApi.key
    ): HistoricalResponse

    @GET("api/v3/search")
    suspend fun search(
        @Query("query") query: String,
        @Query("limit") limit: Int = 10,
        @Query("apikey") apiKey: String = FmpApi.key
    ): List<SearchResponse>
}

object FmpApi {
    const val key: String = BuildConfig.FMP_API_KEY

    val retrofitService: FmpApiService by lazy {
        retrofit.create(FmpApiService::class.java)
    }
}
