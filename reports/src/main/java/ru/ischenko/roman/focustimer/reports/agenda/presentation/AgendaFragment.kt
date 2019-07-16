package ru.ischenko.roman.focustimer.reports.agenda.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import ru.ischenko.roman.focustimer.reports.agenda.presentation.agenda.PomodoroItem
import ru.ischenko.roman.focustimer.reports.databinding.FragmentAgendaBinding
import java.util.*

class AgendaFragment: DaggerFragment() {

    companion object {
        const val TAG = "TAG_AGENDA_FRAGMENT"
        @JvmStatic
        fun newInstance() = AgendaFragment()
    }

    private lateinit var binding: FragmentAgendaBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentAgendaBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = this
        }

        binding.agenda.setPomodoros(
                listOf(
                        PomodoroItem(Date(2019, 7, 14, 15, 20), 25, "Покрасил забор"),
                        PomodoroItem(Date(2019, 7, 14, 15, 50), 25, "Принес воды и еще сделал много разных хорошох дел для себя и других"),
                        PomodoroItem(Date(2019, 7, 14, 17, 30), 25, "Подстриг газон")
                ))

        return binding.root
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