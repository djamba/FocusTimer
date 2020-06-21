package ru.ischenko.roman.focustimer

import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomappbar.BottomAppBar
import ru.ischenko.roman.focustimer.android.OnPressKeyListener
import ru.ischenko.roman.focustimer.presentation.FocusTimerFragment
import ru.ischenko.roman.focustimer.settings.presentation.SettingsFragment

class FocusTimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus_timer)

        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottom_app_bar)
        setSupportActionBar(bottomAppBar)

        var tasksFragment: FocusTimerFragment? =
                supportFragmentManager.findFragmentByTag(FocusTimerFragment.TAG) as FocusTimerFragment?

        if (tasksFragment == null) {
            tasksFragment = FocusTimerFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.container, tasksFragment, FocusTimerFragment.TAG).commit()
        }

        supportFragmentManager.addOnBackStackChangedListener(getBackStackChangedListener())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_appbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) {
            return false
        }
        when (item.itemId) {
            android.R.id.home -> {
                val bottomNavDrawerFragment = BottomMenuDialogFragment()
                bottomNavDrawerFragment.show(supportFragmentManager, bottomNavDrawerFragment.tag)
            }
            R.id.settings_menu -> {
                supportFragmentManager
                        .beginTransaction()
                        .add(R.id.container, SettingsFragment.newInstance(), SettingsFragment.TAG)
                        .addToBackStack(null)
                        .commit()
            }
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val topFragment = getTopFragmentInBackStack()
            if (topFragment is OnPressKeyListener) {
                if (topFragment.onKeyDown(keyCode)) {
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun getTopFragmentInBackStack(): Fragment? {
        val tag = if (supportFragmentManager.backStackEntryCount == 0) {
            FocusTimerFragment.TAG
        } else {
            val index = supportFragmentManager.backStackEntryCount - 1
            supportFragmentManager.getBackStackEntryAt(index).name
        }
        return supportFragmentManager.findFragmentByTag(tag)
    }

    private fun getBackStackChangedListener(): FragmentManager.OnBackStackChangedListener {
        return FragmentManager.OnBackStackChangedListener {
            val index = supportFragmentManager.backStackEntryCount
            if (index >= 0) {
                val fragment: Fragment = supportFragmentManager.fragments[index]
                fragment.onResume()
//                supportFragmentManager.beginTransaction().setMaxLifecycle(fragment, Lifecycle.State.RESUMED).commit()
            }
        }
    }
}
