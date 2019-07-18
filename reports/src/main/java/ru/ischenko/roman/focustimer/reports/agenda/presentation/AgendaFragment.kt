package ru.ischenko.roman.focustimer.reports.agenda.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dagger.android.support.DaggerFragment
import ru.ischenko.roman.focustimer.date.getBeginToday
import ru.ischenko.roman.focustimer.date.getEndToday
import ru.ischenko.roman.focustimer.di.ViewModelFactory
import ru.ischenko.roman.focustimer.di.injectViewModel
import ru.ischenko.roman.focustimer.reports.databinding.FragmentAgendaBinding
import ru.ischenko.roman.focustimer.utils.EventObserver
import javax.inject.Inject

class AgendaFragment: DaggerFragment() {

    companion object {
        const val TAG = "TAG_AGENDA_FRAGMENT"
        @JvmStatic
        fun newInstance() = AgendaFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<AgendaViewModel>
    private val viewModel by lazy { injectViewModel<AgendaViewModel>(viewModelFactory) }

    private lateinit var binding: FragmentAgendaBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentAgendaBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }

        initErrorHandler()

        viewModel.loadPomodorosInPeriod(getBeginToday(), getEndToday())

        return binding.root
    }

    private fun initErrorHandler() {
        viewModel.errorEvent.observe(this, EventObserver { errorText ->
            Toast.makeText(requireContext(), errorText, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onResume() {
        super.onResume()
        scrollToDayTime()
    }

    private fun scrollToDayTime() {
        binding.root.post {
            binding.root.scrollTo(0, (binding.root.bottom * 0.75).toInt())
        }
    }
}