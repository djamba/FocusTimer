package ru.ischenko.roman.focustimer

import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_focus_timer.*
import ru.ischenko.roman.focustimer.presentation.FocusTimerFragment

class FocusTimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus_timer)

        setSupportActionBar(bottom_app_bar)

        var tasksFragment: FocusTimerFragment? =
                supportFragmentManager.findFragmentById(R.id.container) as FocusTimerFragment?

        if (tasksFragment == null) {
            tasksFragment = FocusTimerFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.container, tasksFragment).commit()
        }

        add_goal_action_button.setOnClickListener {
            tasksFragment.onFloatingActionButtonClick()
        }
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
        }
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
