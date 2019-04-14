package ru.ischenko.roman.focustimer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.ischenko.roman.focustimer.di.DaggerBottomSheetDialogFragment
import ru.ischenko.roman.focustimer.di.ViewModelFactory
import ru.ischenko.roman.focustimer.di.injectSharedViewModel
import ru.ischenko.roman.focustimer.timer.databinding.FragmentDialogSetupPomodoroBinding
import javax.inject.Inject

/**
 * User: roman
 * Date: 29.07.18
 * Time: 20:58
 */
class SetupPomodoroDialogFragment : DaggerBottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<FocusTimerViewModel>
    private val viewModel by lazy { injectSharedViewModel<FocusTimerViewModel>(viewModelFactory) }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentDialogSetupPomodoroBinding.inflate(inflater, container, false).also {
            it.viewModel = viewModel
        }

        binding.startButton.setOnClickListener { dismiss() }

        return binding.root
    }

    companion object {

        fun newInstance(): SetupPomodoroDialogFragment {
            return SetupPomodoroDialogFragment()
        }
    }
}
