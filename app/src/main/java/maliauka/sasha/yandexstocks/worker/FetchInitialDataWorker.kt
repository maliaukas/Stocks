package maliauka.sasha.yandexstocks.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import maliauka.sasha.yandexstocks.db.StocksDatabase
import maliauka.sasha.yandexstocks.data.repository.StocksRepository

class FetchInitialDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val database = StocksDatabase.getDatabase(applicationContext)
        val repository = StocksRepository(database)

        try {
            repository.fetchTrendingTickers()
        } catch (e: Exception) {
            return Result.retry()
        }

        return Result.success()
    }

    companion object {
        const val WORK_NAME = "maliauka.sasha.yandexstocks.work.FetchInitialDataWorker"
    }
}

