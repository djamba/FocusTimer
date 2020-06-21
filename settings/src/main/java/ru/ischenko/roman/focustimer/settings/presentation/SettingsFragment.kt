package ru.ischenko.roman.focustimer.settings.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import ru.ischenko.roman.focustimer.di.ViewModelFactory
import ru.ischenko.roman.focustimer.di.injectViewModel
import ru.ischenko.roman.focustimer.settings.databinding.FragmentSettingsBinding
import javax.inject.Inject

class SettingsFragment : DaggerFragment() {

    companion object {
        const val TAG = "TAG_SETTINGS_FRAGMENT"
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SettingsViewModel>
    private val viewModel by lazy { injectViewModel<SettingsViewModel>(viewModelFactory) }

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentSettingsBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }

        binding.settingsToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        return binding.root
    }
}
