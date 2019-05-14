package ru.ischenko.roman.focustimer.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.ischenko.roman.focustimer.di.scope.FragmentScope
import ru.ischenko.roman.focustimer.di.scope.ServiceScope
import ru.ischenko.roman.focustimer.notification.FocusTimerNotificationService
import ru.ischenko.roman.focustimer.presentation.FocusTimerFragment
import ru.ischenko.roman.focustimer.presentation.SetupPomodoroDialogFragment

@Module
interface ContributesInjectorModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [FocusTimerMainModule::class])
    fun contributeFocusTimerFragment(): FocusTimerFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [FocusTimerMainModule::class])
    fun contributeSetupPomodoroDialogFragment(): SetupPomodoroDialogFragment

    @ServiceScope
    @ContributesAndroidInjector
    fun contributeFocusTimerNotificationService(): FocusTimerNotificationService
}