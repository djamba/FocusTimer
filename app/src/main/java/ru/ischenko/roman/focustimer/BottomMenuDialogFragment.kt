package ru.ischenko.roman.focustimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_menu.view.*
import ru.ischenko.roman.focustimer.reports.agenda.presentation.AgendaFragment

class BottomMenuDialogFragment: BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_menu, container, false)

        view.navigation_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.agenda_menu -> openAgenda()
            }
            dismiss()
            true
        }

        return view
    }

    private fun openAgenda() {
        var agendaFragment: AgendaFragment? =
                requireActivity().supportFragmentManager.findFragmentByTag(AgendaFragment.TAG) as AgendaFragment?

        if (agendaFragment == null) {
            agendaFragment = AgendaFragment.newInstance()
        }

        requireActivity().supportFragmentManager.
                beginTransaction().
                add(R.id.container, agendaFragment, AgendaFragment.TAG).
                addToBackStack(null).
                commit()
    }
}