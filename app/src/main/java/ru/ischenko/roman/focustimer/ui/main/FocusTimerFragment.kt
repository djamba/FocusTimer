package ru.ischenko.roman.focustimer.ui.main

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.ischenko.roman.focustimer.R
import ru.ischenko.roman.focustimer.databinding.FragmentFocusTimerBinding

class FocusTimerFragment : Fragment() {

    private lateinit var binding: FragmentFocusTimerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.setContentView(requireActivity(), R.layout.fragment_focus_timer)

        binding.viewModel = FocusTimerViewModel()

        binding.startButton.setOnClickListener { binding.timerView.startTimer(2) }
        binding.stopButton.setOnClickListener { binding.timerView.stopTimer() }

        return inflater.inflate(R.layout.fragment_focus_timer, container, false)
    }

    override fun onResume() {
        super.onResume()
        binding.timerView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.timerView.pause()
    }

    companion object {
        @JvmStatic
        fun newInstance() = FocusTimerFragment()
    }
}
