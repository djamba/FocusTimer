package ru.ischenko.roman.focustimer.ui.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.ischenko.roman.focustimer.databinding.FragmentDialogSetupPomodoroBinding

/**
 * User: roman
 * Date: 29.07.18
 * Time: 20:58
 */
class SetupPomodoroDialogFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: FocusTimerViewModel

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentDialogSetupPomodoroBinding.inflate(inflater, container, false)

        viewModel = ViewModelProviders.of(requireActivity()).get(FocusTimerViewModel::class.java)
        binding.viewModel = viewModel

        binding.startButton.setOnClickListener { dismiss() }

        return binding.root
    }

    companion object {

        fun newInstance(): SetupPomodoroDialogFragment {
            return SetupPomodoroDialogFragment()
        }
    }
}
