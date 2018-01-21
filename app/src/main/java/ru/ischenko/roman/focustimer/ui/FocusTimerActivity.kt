package ru.ischenko.roman.focustimer.ui

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.ischenko.roman.focustimer.R
import ru.ischenko.roman.focustimer.databinding.ActivityFocusTimerBinding

class FocusTimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFocusTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_focus_timer)

        binding.startButton.setOnClickListener { binding.timerView.startTimer(2) }
        binding.stopButton.setOnClickListener { binding.timerView.stopTimer() }
    }

    override fun onResume() {
        super.onResume()
        binding.timerView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.timerView.pause()
    }
}
