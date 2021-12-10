package maliauka.sasha.yandexstocks.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import maliauka.sasha.yandexstocks.db.StocksDatabase.Companion.getDatabase
import maliauka.sasha.yandexstocks.data.repository.StocksRepository
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = StocksRepository(database)

        try {
            repository.refreshStocks()
        } catch (e: HttpException) {
            return Result.retry()
        }

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "maliauka.sasha.yandexstocks.work.RefreshDataWorker"
    }
}
