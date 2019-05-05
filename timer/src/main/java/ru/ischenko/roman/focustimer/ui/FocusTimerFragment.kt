package ru.ischenko.roman.focustimer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import dagger.android.support.DaggerFragment
import ru.ischenko.roman.focustimer.di.ViewModelFactory
import ru.ischenko.roman.focustimer.di.injectSharedViewModel
import ru.ischenko.roman.focustimer.timer.databinding.FragmentFocusTimerBinding
import ru.ischenko.roman.focustimer.utils.EventObserver
import javax.inject.Inject

class FocusTimerFragment : DaggerFragment() {

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
        }

        binding.viewModel = viewModel

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
