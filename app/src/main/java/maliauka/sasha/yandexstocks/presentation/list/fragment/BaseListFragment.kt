package maliauka.sasha.yandexstocks.presentation.list.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import maliauka.sasha.yandexstocks.R
import maliauka.sasha.yandexstocks.data.util.Result
import maliauka.sasha.yandexstocks.databinding.FragmentListBinding
import maliauka.sasha.yandexstocks.presentation.list.StocksAdapter
import maliauka.sasha.yandexstocks.presentation.list.viewmodel.BaseListViewModel
import maliauka.sasha.yandexstocks.presentation.main.MainFragmentDirections
import javax.inject.Inject

abstract class BaseListFragment<VM : BaseListViewModel> : Fragment(R.layout.fragment_list) {

    protected abstract val viewModel: VM

    private val binding by viewBinding<FragmentListBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupObservers()
    }

    @Inject
    lateinit var stocksAdapter: StocksAdapter

    private fun setupAdapter() {
        stocksAdapter.setOnFavButtonClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.onFavButtonClick(it)
                }
            }
        }

        stocksAdapter.setOnLoadImageFailedListener {
            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.setStockColorPlaceHolder(it)
                }
            }
        }

        stocksAdapter.setOnItemClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.displayStockDetails(it)
                }
            }
        }

        binding.rwStocksList.adapter = stocksAdapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stocks.collectLatest {
                    stocksAdapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigateToSelectedStock.collectLatest {
                    if (it != null) {
                        findNavController()
                            .navigate(
                                MainFragmentDirections.actionMainFragmentToStockDetailFragment(
                                    it
                                )
                            )

                        viewModel.displayStockDetailsComplete()
                    }
                }
            }
        }
    }
}

