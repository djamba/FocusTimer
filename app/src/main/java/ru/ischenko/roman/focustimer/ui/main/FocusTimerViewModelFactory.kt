package ru.ischenko.roman.focustimer.ui.main

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import ru.ischenko.roman.focustimer.ui.main.notification.FocusTimerNotification
import ru.ischenko.roman.focustimer.ui.main.notification.StartTimerUseCase
import ru.ischenko.roman.focustimer.utils.ui.ResourceProvider

/**
 * User: roman
 * Date: 18.03.19
 * Time: 22:51
 */
class FocusTimerViewModelFactory(private val startTimerUseCase: StartTimerUseCase,
                                 private val notification: FocusTimerNotification,
                                 private val resourceProvider: ResourceProvider) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(viewModelClass: Class<T>): T {
        return if (viewModelClass == FocusTimerViewModel::class.java) {
            FocusTimerViewModel(startTimerUseCase, notification, resourceProvider) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${viewModelClass.name}")
        }
    }
}