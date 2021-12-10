package maliauka.sasha.yandexstocks.presentation.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import maliauka.sasha.yandexstocks.R

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        updateListPrefSummary()
    }

    private fun updateListPrefSummary() {
        val preference = findPreference<ListPreference>(getString(R.string.prefs_color_theme_key))

        val value = preference?.value ?: getString(R.string.auto_theme_value)

        when (value) {
            getString(R.string.light_theme_value) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                preference?.icon =
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_wb_sunny_24,
                        requireActivity().theme
                    )
            }
            getString(R.string.dark_theme_value) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                preference?.icon =
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_nights_stay_24,
                        requireActivity().theme
                    )
            }
            getString(R.string.auto_theme_value) -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                preference?.icon =
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_baseline_brightness_auto_24,
                        requireActivity().theme
                    )
            }
        }

        val entry = preference?.entry

        preference?.summary =
            String.format(getString(R.string.prefs_color_theme_summary), entry)
    }

    override fun onResume() {
        super.onResume()
        // Set up a listener whenever a key changes
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the listener whenever a key changes
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d("SHAS", "onSharedPreferenceChanged: $key")
        when (key) {
            getString(R.string.prefs_color_theme_key) -> {
                updateListPrefSummary()
            }
        }
    }
}
