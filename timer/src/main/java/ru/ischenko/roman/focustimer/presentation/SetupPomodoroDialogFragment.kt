package ru.ischenko.roman.focustimer.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.ischenko.roman.focustimer.di.DaggerBottomSheetDialogFragment
import ru.ischenko.roman.focustimer.di.ViewModelFactory
import ru.ischenko.roman.focustimer.di.injectSharedViewModel
import ru.ischenko.roman.focustimer.timer.R
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

        binding.okButton.setOnClickListener {
            viewModel.handleUpdateTaskGoal()
            dismiss()
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val setupPomodoroDialog = super.onCreateDialog(savedInstanceState)
        setupPomodoroDialog.setOnShowListener { dialog ->

            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)

            val behaviour = BottomSheetBehavior.from(bottomSheet)
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED

            behaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        viewModel.handleUpdateTaskGoal()
                        dismiss()
                    }
                }
            })
        }

        return setupPomodoroDialog
    }

    companion object {

        fun newInstance(): SetupPomodoroDialogFragment {
            return SetupPomodoroDialogFragment()
        }
    }
}
