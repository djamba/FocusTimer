package ru.ischenko.roman.focustimer.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.android.support.DaggerFragment
import ru.ischenko.roman.focustimer.android.OnPressKeyListener
import ru.ischenko.roman.focustimer.di.ViewModelFactory
import ru.ischenko.roman.focustimer.di.injectSharedViewModel
import ru.ischenko.roman.focustimer.timer.databinding.FragmentFocusTimerBinding
import ru.ischenko.roman.focustimer.utils.EventObserver
import javax.inject.Inject

class FocusTimerFragment : DaggerFragment(), OnPressKeyListener {

    companion object {
        const val TAG = "TAG_FOCUS_TIMER_FRAGMENT"
        @JvmStatic
        fun newInstance() = FocusTimerFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<FocusTimerViewModel>
    private val viewModel by lazy { injectSharedViewModel<FocusTimerViewModel>(viewModelFactory) }

    private lateinit var binding: FragmentFocusTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentFocusTimerBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }

        initHandlers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = (requireActivity() as AppCompatActivity)
        activity.setSupportActionBar(binding.bottomAppBar)
    }

    private fun initHandlers() {

        viewModel.goal.observe(this::getLifecycle) {
            binding.invalidateAll()
        }

        viewModel.startTimerEvent.observe(this, EventObserver {
            binding.componentTimerView.timerView.startTimer(it)
        })

        viewModel.editGoalTextEvent.observe(this, EventObserver {
            val addPhotoBottomDialogFragment = SetupPomodoroDialogFragment.newInstance()
            addPhotoBottomDialogFragment.show(childFragmentManager, null)
        })

        viewModel.errorEvent.observe(this, EventObserver { errorText ->
            Toast.makeText(requireContext(), errorText, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onKeyDown(keyCode: Int): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && viewModel.uiState.value != UiState.STOPPED) {
            requireActivity().moveTaskToBack(true)
            return true
        }
        return false
    }
}
