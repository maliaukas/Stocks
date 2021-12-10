package maliauka.sasha.yandexstocks.presentation.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import maliauka.sasha.yandexstocks.presentation.list.fragment.FavStocksListFragment
import maliauka.sasha.yandexstocks.presentation.list.fragment.StockListFragment


class SectionsPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> StockListFragment.getInstance()
            1 -> FavStocksListFragment.getInstance()
            else -> throw IllegalArgumentException("A!!! Wrong position: $position")
        }
    }
}
