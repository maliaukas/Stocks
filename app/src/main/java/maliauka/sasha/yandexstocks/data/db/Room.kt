package maliauka.sasha.yandexstocks.db;


import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import maliauka.sasha.yandexstocks.data.db.entity.DatabaseStock

@Dao
interface StockDao {

    @Query("delete from stocks")
    fun deleteAll()

    @Query("select * from stocks where lastUpdateDate < :date")
    fun getStocksToUpdate(date: String): List<DatabaseStock>

    @Query("select * from stocks order by ticker")
    fun getStocksFlow(): Flow<List<DatabaseStock>>

    @Query("select * from stocks where isFavourite = 1 order by ticker")
    fun getFavouriteStocksFlow(): Flow<List<DatabaseStock>>

    @Query("select * from stocks where ticker like :query or companyName like :query order by ticker")
    fun search(query: String): Flow<List<DatabaseStock>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(stocks: List<DatabaseStock>)

    @Query("update stocks set isFavourite = 1 where ticker = :stockTicker")
    fun makeFavourite(stockTicker: String)

    @Query("update stocks set isFavourite = 0 where ticker = :stockTicker")
    fun unFavourite(stockTicker: String)

    @Query("select isFavourite from stocks where ticker = :stockTicker")
    fun isFavourite(stockTicker: String): Boolean

    @Query("select * from stocks where ticker in (:tickers)")
    fun getStocksForTickers(tickers: List<String>): List<DatabaseStock>

    @Query(
        "update stocks set " +
                "price = :price, " +
                "delta = :delta, " +
                "deltaPercent = :deltaPercent, " +
                "lastUpdateDate = :date " +
                "where ticker = :stockTicker"
    )
    fun updateStockPrice(
        stockTicker: String,
        price: Double,
        delta: Double,
        deltaPercent: Double,
        date: String
    )

    @Query("select count(*) from stocks")
    fun getRecordsCount(): Int

    @Query("update stocks set placeHolderColor = :randomColor where ticker = :ticker")
    fun setStockColorPlaceHolder(ticker: String, randomColor: Int)

    @Query("select * from stocks where ticker = :ticker")
    fun getStock(ticker: String): Flow<DatabaseStock>
}

@Database(entities = [DatabaseStock::class], version = 1)
abstract class StocksDatabase : RoomDatabase() {
    abstract val stockDao: StockDao

    companion object {
        @Volatile
        private var INSTANCE: StocksDatabase? = null

        fun getDatabase(context: Context): StocksDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StocksDatabase::class.java,
                    "stock_database"
                ).build()

                INSTANCE = instance

                return@synchronized instance
            }
        }
    }
}
