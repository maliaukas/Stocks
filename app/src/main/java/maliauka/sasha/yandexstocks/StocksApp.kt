package maliauka.sasha.yandexstocks

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import maliauka.sasha.yandexstocks.worker.FetchInitialDataWorker
import maliauka.sasha.yandexstocks.worker.RefreshDataWorker
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class StocksApp : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        delayedInit()
    }

    private fun delayedInit() {
        applicationScope.launch {
            executeOneTimeWork()
            setupRecurringWork()
        }
    }

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .build()

    private fun executeOneTimeWork() {
        val oneTimeWorkRequest =
            OneTimeWorkRequestBuilder<FetchInitialDataWorker>().setConstraints(constraints).build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                FetchInitialDataWorker.WORK_NAME,
                ExistingWorkPolicy.KEEP,
                oneTimeWorkRequest
            )
    }

    /**
     * Setup WorkManager background job to 'fetch' new network data daily.
     */
    private fun setupRecurringWork() {
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(
            1,
            TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                RefreshDataWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                repeatingRequest
            )
    }
}
