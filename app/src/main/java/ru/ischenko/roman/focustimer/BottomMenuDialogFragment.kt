package ru.ischenko.roman.focustimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_menu.view.*
import ru.ischenko.roman.focustimer.reports.agenda.presentation.AgendaFragment
import ru.ischenko.roman.focustimer.settings.presentation.SettingsFragment

class BottomMenuDialogFragment: BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_menu, container, false)

        view.navigation_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.agenda_menu -> openAgenda()
                R.id.settings_menu -> openSettingsMenu()
            }
            dismiss()
            true
        }

        return view
    }

    private fun openAgenda() {
        requireActivity().supportFragmentManager
                .beginTransaction()
                .add(R.id.container, AgendaFragment.newInstance(), AgendaFragment.TAG)
                .addToBackStack(null)
                .commit()
    }

    private fun openSettingsMenu() {
        requireActivity().supportFragmentManager
                .beginTransaction()
                .add(R.id.container, SettingsFragment.newInstance(), SettingsFragment.TAG)
                .addToBackStack(null)
                .commit()
    }
}
