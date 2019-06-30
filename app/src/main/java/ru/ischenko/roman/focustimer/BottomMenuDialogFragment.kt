package ru.ischenko.roman.focustimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_bottom_menu.view.*

class BottomMenuDialogFragment: BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_menu, container, false)

        view.navigation_view.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                // TODO: open today agenda screen
                R.id.agenda_menu -> Toast.makeText(requireContext(), "Agenda", Toast.LENGTH_SHORT).show()
            }
            dismiss()
            true
        }

        return view
    }
}