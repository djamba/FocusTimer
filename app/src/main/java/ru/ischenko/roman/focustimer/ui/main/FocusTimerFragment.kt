package ru.ischenko.roman.focustimer.ui.main

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
import ru.ischenko.roman.focustimer.ui.main.notification.FocusTimerNotificationImpl
import ru.ischenko.roman.focustimer.ui.main.notification.FocusTimerNotificationService
import ru.ischenko.roman.focustimer.ui.main.notification.StartTimerUseCase
import ru.ischenko.roman.focustimer.utils.ui.EventObserver
import ru.ischenko.roman.focustimer.utils.ui.ResourceProvider

class FocusTimerFragment : Fragment(), FocusTimerNotificationService.OnTimeChangedListener {

    private var serviceBound = false
    private var focusTimerNotificationService: FocusTimerNotificationService? = null

    private lateinit var binding: FragmentFocusTimerBinding
    private lateinit var viewModel: FocusTimerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentFocusTimerBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = this
        }

        val focusTimerNotification = FocusTimerNotificationImpl(requireContext().applicationContext)
        val resourceProvider = ResourceProvider(requireContext().applicationContext)
        val startTimerUseCase = StartTimerUseCase(requireContext().applicationContext)
        val viewModelFactory = FocusTimerViewModelFactory(startTimerUseCase, focusTimerNotification, resourceProvider)

        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(FocusTimerViewModel::class.java)
        binding.viewModel = viewModel


        // TODO: Перенести во ViewModel
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

        initHandlers()

        return binding.root
    }

    private fun initHandlers() {

        viewModel.goal.observe(this::getLifecycle) {
            binding.invalidateAll()
        }

        viewModel.startTimerEvent.observe(this, EventObserver {
            binding.timerView.startTimer(it)
        })
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
        viewModel.showNotification()
    }

    override fun onTimerCancel() {
        binding.timerView.stopTimer()
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
