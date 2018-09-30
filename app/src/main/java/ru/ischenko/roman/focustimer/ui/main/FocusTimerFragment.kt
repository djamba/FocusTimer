package ru.ischenko.roman.focustimer.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ru.ischenko.roman.focustimer.databinding.FragmentFocusTimerBinding
import ru.ischenko.roman.focustimer.ui.common.TimerView

class FocusTimerFragment : Fragment() {

    private lateinit var binding: FragmentFocusTimerBinding
    private lateinit var viewModel: FocusTimerViewModel
    private lateinit var focusTimerNotification: FocusTimerNotification

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentFocusTimerBinding.inflate(inflater, container, false)

        viewModel = ViewModelProviders.of(requireActivity()).get(FocusTimerViewModel::class.java)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        viewModel.goal.observe(this, Observer { binding.invalidateAll() })

        focusTimerNotification = FocusTimerNotification(requireContext())

        binding.startButton.setOnClickListener {
            binding.timerView.startTimer(1)
            focusTimerNotification.notifyFocusOnWork(viewModel.goal.value ?: "focus")
        }

        binding.stopButton.setOnClickListener {
            binding.timerView.stopTimer()
            focusTimerNotification.cancel()
        }

        binding.pauseButton.setOnClickListener {
            if (binding.timerView.isRunning()) {
                binding.timerView.pauseTimer()
            } else {
                binding.timerView.resumeTimer()
            }
        }

        binding.goalText.setOnClickListener {
            val addPhotoBottomDialogFragment = SetupPomodoroDialogFragment.newInstance()
            addPhotoBottomDialogFragment.show(requireActivity().supportFragmentManager, null)
        }

        binding.timerView.onTimeViewListener = object: TimerView.OnTimeViewListener {
            override fun onComplete() {
                Toast.makeText(context, "Complete!", Toast.LENGTH_SHORT).show()
                focusTimerNotification.finish()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.timerView.onForeground()
    }

    override fun onPause() {
        super.onPause()
        binding.timerView.onBackground()
    }

    companion object {
        @JvmStatic
        fun newInstance() = FocusTimerFragment()
    }
}
