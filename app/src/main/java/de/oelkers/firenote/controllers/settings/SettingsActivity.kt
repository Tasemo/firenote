package de.oelkers.firenote.controllers.settings

import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.commit
import androidx.preference.PreferenceFragmentCompat
import de.oelkers.firenote.R
import de.oelkers.firenote.util.AppBarActivity

class SettingsActivity : AppBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.settings_content, SettingsFragment())
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.settings).setVisible(false)
        return super.onPrepareOptionsMenu(menu)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }
}
