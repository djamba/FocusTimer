package ru.ischenko.roman.focustimer.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ru.ischenko.roman.focustimer.databinding.FragmentFocusTimerBinding
import ru.ischenko.roman.focustimer.ui.common.TimerView
import ru.ischenko.roman.focustimer.ui.main.notification.FocusTimerNotificationService

class FocusTimerFragment : Fragment(), FocusTimerNotificationService.OnTimeChangedListener {

    private var serviceBound = false
    private var focusTimerNotificationService: FocusTimerNotificationService? = null

    private lateinit var binding: FragmentFocusTimerBinding
    private lateinit var viewModel: FocusTimerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentFocusTimerBinding.inflate(inflater, container, false)

        viewModel = ViewModelProviders.of(requireActivity()).get(FocusTimerViewModel::class.java)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        viewModel.goal.observe(this, Observer { binding.invalidateAll() })

        binding.startButton.setOnClickListener {
            binding.timerView.startTimer(60)
            FocusTimerNotificationService.showNotification(
                    requireContext(),
                    viewModel.goal.value ?: "focus",
                    System.currentTimeMillis(),
                    60)
        }

        binding.stopButton.setOnClickListener {
            binding.timerView.stopTimer()
            FocusTimerNotificationService.cancelNotification(requireContext())
        }

        binding.pauseButton.setOnClickListener {
            FocusTimerNotificationService.pauseResumeWork(requireContext())
        }

        binding.goalText.setOnClickListener {
            val addPhotoBottomDialogFragment = SetupPomodoroDialogFragment.newInstance()
            addPhotoBottomDialogFragment.show(requireActivity().supportFragmentManager, null)
        }

        binding.timerView.onTimeViewListener = object: TimerView.OnTimeViewListener {
            override fun onComplete() {
                Toast.makeText(context, "Complete!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(requireContext(), FocusTimerNotificationService::class.java)
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (serviceBound) {
            requireActivity().unbindService(serviceConnection)
            serviceBound = false
        }
    }

    override fun onTimeChanged(timerSecondsPassed: Long) {
        binding.timerView.updateTime(timerSecondsPassed)
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            val notificationServiceBinder = binder as FocusTimerNotificationService.NotificationServiceBinder
            focusTimerNotificationService = notificationServiceBinder.service
            focusTimerNotificationService?.onTimeChangedListener = this@FocusTimerFragment
            serviceBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            focusTimerNotificationService?.onTimeChangedListener = null
            serviceBound = false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = FocusTimerFragment()
    }
}
