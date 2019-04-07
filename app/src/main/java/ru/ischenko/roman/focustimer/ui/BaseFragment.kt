package ru.ischenko.roman.focustimer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import ru.ischenko.roman.focustimer.utils.ui.ViewModelFactory

/**
 * User: roman
 * Date: 07.04.19
 * Time: 21:14
 */
open class BaseFragment : DaggerFragment() {

    protected inline fun <reified T : ViewModel> getViewModel(viewModelFactory: ViewModelFactory<T>): T {
        return ViewModelProviders.of(this, viewModelFactory).get(T::class.java)
    }
}