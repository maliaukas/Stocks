package maliauka.sasha.yandexstocks.presentation.list.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import maliauka.sasha.yandexstocks.presentation.list.viewmodel.FavStocksViewModel

@AndroidEntryPoint
class FavStocksListFragment : BaseListFragment<FavStocksViewModel>() {
    override val viewModel: FavStocksViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewLifecycleOwner.lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.showErrorMessage.collectLatest { (show, message) ->
//                    if (show) {
//                        Toast.makeText(
//                            requireContext(),
//                            "Error occurred: $message",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.showProgressBar.collectLatest { show ->
//                    binding.progressBar.isVisible = show
//                }
//            }
//        }
    }

    companion object {
        @JvmStatic
        fun getInstance(): FavStocksListFragment {
            return FavStocksListFragment()
        }
    }
}
