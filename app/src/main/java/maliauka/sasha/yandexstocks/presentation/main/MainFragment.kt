package maliauka.sasha.yandexstocks.presentation.main

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import maliauka.sasha.yandexstocks.R
import maliauka.sasha.yandexstocks.databinding.FragmentMainBinding


class MainFragment : Fragment(R.layout.fragment_main) {
    private val binding by viewBinding<FragmentMainBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionsPagerAdapter =
            SectionsPagerAdapter(childFragmentManager, lifecycle)

        binding.viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs.tabLayout

        TabLayoutMediator(tabs, binding.viewPager) { tab, position ->

            when (position) {
                0 -> tab.text = getString(R.string.tab_text_stocks)
                1 -> tab.text = getString(R.string.tab_text_favourite)
            }
        }.attach()


        tabs.getTabAt(0)?.customView = createCustomTabView("Stocks", 28, selectedTabColor)
        tabs.getTabAt(1)?.customView = createCustomTabView("Favourite", 18, generalTabColor)

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { setTabTextSize(it, 28, selectedTabColor) }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let { setTabTextSize(it, 18, generalTabColor) }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) = Unit

        })
    }

    private fun createCustomTabView(tabText: String, tabSizeSp: Int, color: Int): View {
        val tabCustomView: View =
            layoutInflater.inflate(R.layout.tab_customview, null)

        val tabTextView =
            tabCustomView.findViewById<AppCompatTextView>(R.id.tv_tab)

        tabTextView.text = tabText
        tabTextView.textSize = tabSizeSp.toFloat()
        tabTextView.setTextColor(color)

        return tabCustomView
    }

    private fun setTabTextSize(tab: TabLayout.Tab, tabSizeSp: Int, textColor: Int) {
        val tabCustomView = tab.customView
        val tabTextView =
            tabCustomView!!.findViewById<AppCompatTextView>(R.id.tv_tab)

        tabTextView.textSize = tabSizeSp.toFloat()
        tabTextView.height
        tabTextView.setTextColor(textColor)
    }

    private val selectedTabColor: Int by lazy {
        val typedValue = TypedValue()
        val theme = requireActivity().theme

        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)
    }

    private val generalTabColor: Int by lazy {
        val typedValue = TypedValue()
        val theme = requireActivity().theme

        theme.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)
    }

}
