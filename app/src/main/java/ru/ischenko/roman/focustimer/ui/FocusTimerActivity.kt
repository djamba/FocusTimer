package ru.ischenko.roman.focustimer.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import ru.ischenko.roman.focustimer.R
import ru.ischenko.roman.focustimer.ui.main.FocusTimerFragment

class FocusTimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focus_timer)

        var tasksFragment: FocusTimerFragment? =
                supportFragmentManager.findFragmentById(R.id.container) as FocusTimerFragment?

        if (tasksFragment == null) {
            tasksFragment = FocusTimerFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.container, tasksFragment).commit()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}
