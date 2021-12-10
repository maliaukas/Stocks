package maliauka.sasha.yandexstocks.presentation.search

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import maliauka.sasha.yandexstocks.R
import maliauka.sasha.yandexstocks.databinding.FragmentSearchBinding
import maliauka.sasha.yandexstocks.presentation.list.StocksAdapter
import maliauka.sasha.yandexstocks.presentation.list.viewmodel.StocksViewModel
import maliauka.sasha.yandexstocks.presentation.main.MainFragmentDirections
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private val binding by viewBinding<FragmentSearchBinding>()

    private val searchViewModel: SearchViewModel by activityViewModels()

    @Inject
    lateinit var stocksAdapter: StocksAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHintsAdapter()
        setupStocksAdapter()
        setupObservers()
    }

    private fun setupStocksAdapter() {
        stocksAdapter.setOnFavButtonClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    searchViewModel.onFavButtonClick(it)
                }
            }
        }

        stocksAdapter.setOnLoadImageFailedListener {
            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    searchViewModel.setStockColorPlaceHolder(it)
                }
            }
        }

        binding.resultList.rwStocksList.adapter = stocksAdapter
    }

    private fun setupHintsAdapter() {
        binding.rvPopular.adapter = SearchHintsAdapter {
            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    searchViewModel.search(it)
                }
            }
        }.also {
            it.submitList(
                listOf(
                    "Apple",
                    "Amazon",
                    "Alphabet",
                    "Tesla",
                    "Alibaba",
                    "Facebook",
                    "Mastercard",
                    "Microsoft",
                    "Nokia",
                    "Yandex",
                    "Nvidia",
                    "Intel",
                    "AMD",
                    "Baidu",
                    "Visa"
                ).sorted()
            )
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.searchResult.collectLatest {
                    stocksAdapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.showApiCallsMessage.collectLatest { show ->
                    if (show) {
                        Snackbar.make(
                            requireView(),
                            getString(R.string.api_calls_message),
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.showErrorMessage.collectLatest { (show, message) ->
                    if (show) {
                        Snackbar.make(
                            binding.root,
                            "Error occurred : $message!",
                            Snackbar.LENGTH_INDEFINITE
                        ).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.showProgressBar.collectLatest { show ->
                    binding.progressBar.isVisible = show
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.showEmptyMessage.collectLatest { show ->
                    binding.tvNothingFound.isVisible = show
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.showResultList.collectLatest { show ->
                    if (show) {
                        binding.groupResultList.visibility = View.VISIBLE
                        binding.groupPopular.visibility = View.GONE
                        //binding.tvNothingFound.visibility = View.VISIBLE
                    } else {
                        binding.groupResultList.visibility = View.GONE
                        binding.groupPopular.visibility = View.VISIBLE
                        binding.tvNothingFound.visibility = View.GONE
                    }
                }
            }
        }
    }
}
