package ru.ischenko.roman.focustimer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.ischenko.roman.focustimer.databinding.FragmentFocusTimerBinding
import ru.ischenko.roman.focustimer.ui.BaseFragment
import ru.ischenko.roman.focustimer.utils.ui.EventObserver
import ru.ischenko.roman.focustimer.utils.ui.ViewModelFactory
import javax.inject.Inject

class FocusTimerFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<FocusTimerViewModel>

    private lateinit var binding: FragmentFocusTimerBinding
    private lateinit var viewModel: FocusTimerViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentFocusTimerBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = this
        }

        viewModel = getViewModel(viewModelFactory)
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
