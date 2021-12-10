package maliauka.sasha.yandexstocks

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import maliauka.sasha.yandexstocks.databinding.ActivityMainBinding
import maliauka.sasha.yandexstocks.presentation.search.SearchViewModel
import maliauka.sasha.yandexstocks.worker.FetchInitialDataWorker
import maliauka.sasha.yandexstocks.worker.RefreshDataWorker
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import maliauka.sasha.yandexstocks.presentation.main.MainFragmentDirections

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val searchViewModel: SearchViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private fun hideToolbar() {
        binding.toolbar.isVisible = false
    }

    private fun showToolbar() {
        binding.toolbar.isVisible = true
    }

    private fun hideFabSettings() {
        binding.fabSettings.isVisible = false
    }

    private fun showFabSettings() {
        binding.fabSettings.isVisible = true
    }

    private fun hideFabHome() {
        binding.fabHome.isVisible = false
    }

    private fun showFabHome() {
        binding.fabHome.isVisible = true
    }

    override fun onStart() {
        super.onStart()

        setupNavController()
        setupOnClickListeners()
    }

    private fun setupNavController() {

        navController = Navigation.findNavController(this@MainActivity, R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.searchFragment -> {
                    hideFabSettings()
                    hideFabHome()

                    (binding.toolbar.layoutParams as AppBarLayout.LayoutParams).scrollFlags = 0
                }
                R.id.mainFragment -> {
                    hideFabHome()
                    showFabSettings()
                    showToolbar()

                    (binding.toolbar.layoutParams as AppBarLayout.LayoutParams).scrollFlags =
                        AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or
                                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL

                    clearSearchFocus()
                }
                R.id.settingsFragment -> {
                    hideFabSettings()
                    hideToolbar()

                    showFabHome()

                    (binding.toolbar.layoutParams as AppBarLayout.LayoutParams).scrollFlags = 0
                }
                R.id.stockDetailFragment -> {
                    hideFabHome()
                    hideFabSettings()
                    hideToolbar()

                    (binding.toolbar.layoutParams as AppBarLayout.LayoutParams).scrollFlags = 0
                }
            }
        }

        appBarConfiguration = AppBarConfiguration(navController.graph)
    }

    private fun setupOnClickListeners() {

        binding.fabSettings.setOnClickListener {
            navController.navigate(R.id.action_global_settingsFragment)
        }

        binding.fabHome.setOnClickListener {
            navController.navigate(R.id.action_global_mainFragment)
        }
    }

    private fun applyTheme() {
        val theme = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(
                getString(R.string.prefs_color_theme_key),
                getString(R.string.prefs_color_theme_default)
            )

        Log.d("SHAS", "theme = $theme")

        when (theme) {
            getString(R.string.light_theme_value) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            getString(R.string.dark_theme_value) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            getString(R.string.auto_theme) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        applyTheme()
        setContentView(binding.root)

        setupSearchBar()
        setupObservers()

        setupWorkManagerObservers()
    }

    private fun setupWorkManagerObservers() {
        WorkManager
            .getInstance(applicationContext)
            .getWorkInfosForUniqueWorkLiveData(FetchInitialDataWorker.WORK_NAME)
            .observe(this) { workInfoList ->
                Log.d(
                    "SHAS",
                    "Observe FetchInitialDataWorker workInfoList\n" + workInfoList.joinToString("\n")
                )
                binding.progressBar.visibility = when (workInfoList.first().state) {
                    WorkInfo.State.RUNNING -> View.VISIBLE
                    else -> View.INVISIBLE
                }
            }

        WorkManager
            .getInstance(applicationContext)
            .getWorkInfosForUniqueWorkLiveData(RefreshDataWorker.WORK_NAME)
            .observe(this) { workInfoList ->
                Log.d(
                    "SHAS",
                    "Observe RefreshDataWorker workInfoList\n" + workInfoList.joinToString("\n")
                )
                binding.progressBar.visibility = when (workInfoList.first().state) {
                    WorkInfo.State.RUNNING -> View.VISIBLE
                    else -> View.INVISIBLE
                }
            }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // observe query changes
                searchViewModel.query.collectLatest { result: String ->
                    binding.search.etSearchBar.setText(result)
                    binding.search.etSearchBar.setSelection(result.length)
                }
            }
        }
    }

    var handler: Handler = Handler(Looper.getMainLooper())
    var workRunnable: Runnable? = null


    private fun setupSearchBar() {
        binding.search.apply {

            etSearchBar.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

                override fun afterTextChanged(editable: Editable?) {
                    // https://stackoverflow.com/a/35268540
                    workRunnable?.let { handler.removeCallbacks(it) }
                    workRunnable = Runnable { handleInput(editable.toString()) }
                    handler.postDelayed(workRunnable!!, 500 /*delay*/)
                }
            })

            ibClearSearch.setOnClickListener { clearSearchText() }

            fun changeLeftIcon(iconState: IconState) {
                when (iconState) {
                    IconState.BACK -> {
                        ivLeftIcon.setImageResource(R.drawable.ic_back)
                        ivLeftIcon.setOnClickListener {
                            clearSearchFocus()
                            navController.navigateUp(appBarConfiguration)
                        }
                    }
                    IconState.SEARCH -> {
                        ivLeftIcon.setImageResource(R.drawable.ic_search)
                        ivLeftIcon.setOnClickListener { }
                    }
                }
            }

            etSearchBar.onFocusChangeListener =
                View.OnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        changeLeftIcon(IconState.BACK)

                        navController.navigate(MainFragmentDirections.actionMainFragmentToSearchFragment())
                    } else {
                        changeLeftIcon(IconState.SEARCH)
                    }
                }
        }
    }

    private fun handleInput(input: String) {
        if (input.isBlank()) {
            // when search query is empty:
            // 1. hide button "clear"
            binding.search.ibClearSearch.isVisible = false
            // 2. hide result list section in fragment
            searchViewModel.setShowResultList(false)
        } else {
            // when search query is not empty:
            // 1. show button "clear"
            binding.search.ibClearSearch.isVisible = true
            // 2. show result list section in fragment
            searchViewModel.setShowResultList(true)
            // 3. execute search
            searchViewModel.search(input)
        }
    }

    private fun clearSearchText() {
        binding.search.etSearchBar.text.clear()
    }

    private fun clearSearchFocus() {
        binding.search.etSearchBar.text.clear()
        binding.search.etSearchBar.hideKeyboard()
        binding.search.etSearchBar.clearFocus()
    }

    enum class IconState {
        BACK,
        SEARCH
    }

    override fun onPause() {
        super.onPause()
        workRunnable?.let { handler.removeCallbacks(it) }
    }


    private fun View.hideKeyboard() = ViewCompat.getWindowInsetsController(this)
        ?.hide(WindowInsetsCompat.Type.ime())
}


