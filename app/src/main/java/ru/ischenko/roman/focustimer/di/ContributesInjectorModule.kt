package ru.ischenko.roman.focustimer.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.ischenko.roman.focustimer.notification.FocusTimerNotificationService
import ru.ischenko.roman.focustimer.ui.FocusTimerFragment
import ru.ischenko.roman.focustimer.ui.di.FocusTimerMainModule

@Module
interface ContributesInjectorModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [FocusTimerMainModule::class])
    fun contributeFocusTimerFragment(): FocusTimerFragment

    @ContributesAndroidInjector
    fun contributeFocusTimerNotificationService(): FocusTimerNotificationService
}