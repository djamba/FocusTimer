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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ru.ischenko.roman.focustimer.R
import ru.ischenko.roman.focustimer.databinding.FragmentFocusTimerBinding
import ru.ischenko.roman.focustimer.ui.common.TimerView
import ru.ischenko.roman.focustimer.ui.main.notification.*

class FocusTimerFragment : Fragment(), FocusTimerNotificationService.OnTimeChangedListener {

    private val POMODORE_TIME = 10L //TimeUnit.MINUTES.toSeconds(25)
    private val REST_TIME = 5L //TimeUnit.MINUTES.toSeconds(5)

    private val ACTION_REST: String = "ACTION_REST"
    private val ACTION_WORK: String = "ACTION_WORK"

    private var isWorkTime = true

    private var serviceBound = false
    private var focusTimerNotificationService: FocusTimerNotificationService? = null

    private lateinit var binding: FragmentFocusTimerBinding
    private lateinit var viewModel: FocusTimerViewModel
    private lateinit var focusTimerNotification: FocusTimerNotification

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentFocusTimerBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = this
        }

        focusTimerNotification = FocusTimerNotification(requireContext())
        focusTimerNotification.focusTimerNotificationListener = object : FocusTimerNotification.FocusTimerNotificationListener {
            override fun onResume() {
                Log.d("TUT", "onResume()")
            }

            override fun onCancel() {
                Log.d("TUT", "onCancel()")
            }

            override fun onPause() {
                Log.d("TUT", "onPause()")
            }

            override fun onCustomAction(action: String) {
                Log.d("TUT", "onCustomAction(" + action + ")")

                when (action) {
                     ACTION_REST ->
                        startTimer(REST_TIME,
                                getString(R.string.focus_timer_notification_rest),
                                getString(R.string.focus_timer_notification_rest_content))
                     ACTION_WORK ->
                        startTimer(POMODORE_TIME, getString(R.string.focus_timer_notification_focus_on_work),
                            viewModel.goal.value ?: "focus")
                }
            }
        }

        viewModel = ViewModelProviders.of(requireActivity()).get(FocusTimerViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.goal.observe(this, Observer { binding.invalidateAll() })

        binding.startButton.setOnClickListener {
            startTimer(POMODORE_TIME, getString(R.string.focus_timer_notification_focus_on_work),
                    viewModel.goal.value ?: "focus")
        }

        binding.stopButton.setOnClickListener {
            binding.timerView.stopTimer()
            FocusTimerNotificationService.cancelNotification(requireContext())
        }

        binding.pauseButton.setOnClickListener {
            FocusTimerNotificationService.resumePauseTimer(requireContext())
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

    override fun onResume() {
        super.onResume()
        focusTimerNotification.register()
    }

    override fun onPause() {
        super.onPause()
        focusTimerNotification.unregister()
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

    override fun onTimerPaused() {
        binding.pauseButton.text = "Resume"
    }

    override fun onTimerResumed() {
        binding.pauseButton.text = "Pause"
    }

    override fun onTimerFinish() {
        binding.timerView.stopTimer()

        if (isWorkTime) {
            showNotification(getString(R.string.focus_timer_notification_rest),
                             getString(R.string.focus_timer_notification_rest_content))
        } else{
            showNotification(getString(R.string.focus_timer_notification_focus_on_work),
                    viewModel.goal.value ?: "focus")
        }

        isWorkTime = !isWorkTime
    }

    override fun onTimerCancel() {
        binding.timerView.stopTimer()
    }

    private fun startTimer(time: Long, title: String, message: String) {
        binding.timerView.startTimer(time)
        FocusTimerNotificationService.startTimer(
                requireContext(), title, message, System.currentTimeMillis(), time,
                arrayOf(CancelAction, ResumePauseAction))
    }

    private fun showNotification(title: String, message: String) {

        val actions = if (isWorkTime) {
            listOf(CancelAction, CustomAction(ACTION_REST, "Rest"))
        } else {
            listOf(CancelAction, CustomAction(ACTION_WORK, "Work"))
        }

        val notification = focusTimerNotification.createNotification(title, message, false, actions)
        focusTimerNotification.notify(notification)
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
