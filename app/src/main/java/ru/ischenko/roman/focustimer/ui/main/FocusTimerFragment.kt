package ru.ischenko.roman.focustimer.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.ischenko.roman.focustimer.databinding.FragmentFocusTimerBinding
import ru.ischenko.roman.focustimer.ui.notification.*
import ru.ischenko.roman.focustimer.utils.ui.EventObserver
import ru.ischenko.roman.focustimer.utils.ui.ResourceProvider

class FocusTimerFragment : Fragment() {

    private lateinit var binding: FragmentFocusTimerBinding
    private lateinit var viewModel: FocusTimerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentFocusTimerBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = this
        }

        // TODO: Убрать инициализацию в DI
        val focusTimerNotification = FocusTimerNotificationImpl(requireContext().applicationContext)
        val resourceProvider = ResourceProvider(requireContext().applicationContext)
        val startTimerUseCase = StartTimerUseCase(requireContext().applicationContext)
        val stopTimerUseCase = StopTimerUseCase(requireContext().applicationContext)
        val resumePauseTimerUseCase = ResumePauseTimerUseCase(requireContext().applicationContext)
        val notificationServiceDelegate = NotificationServiceDelegateImpl(requireContext().applicationContext)

        val viewModelFactory = FocusTimerViewModelFactory(startTimerUseCase, stopTimerUseCase, resumePauseTimerUseCase,
                focusTimerNotification, notificationServiceDelegate, resourceProvider)

        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(FocusTimerViewModel::class.java)
        binding.viewModel = viewModel

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

        viewModel.editGoalTextEvent.observe(this, EventObserver {
            val addPhotoBottomDialogFragment = SetupPomodoroDialogFragment.newInstance()
            addPhotoBottomDialogFragment.show(requireActivity().supportFragmentManager, null)
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = FocusTimerFragment()
    }
}
