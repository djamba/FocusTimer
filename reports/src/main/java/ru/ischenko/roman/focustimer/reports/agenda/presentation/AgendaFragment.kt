package ru.ischenko.roman.focustimer.reports.agenda.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import ru.ischenko.roman.focustimer.reports.databinding.FragmentAgendaBinding

class AgendaFragment: DaggerFragment() {

    companion object {
        const val TAG = "TAG_AGENDA_FRAGMENT"
        @JvmStatic
        fun newInstance() = AgendaFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentAgendaBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = this
        }

        return binding.root
    }
}