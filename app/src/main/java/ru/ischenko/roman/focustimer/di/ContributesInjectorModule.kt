package ru.ischenko.roman.focustimer.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.ischenko.roman.focustimer.di.scope.FragmentScope
import ru.ischenko.roman.focustimer.di.scope.ServiceScope
import ru.ischenko.roman.focustimer.notification.FocusTimerNotificationService
import ru.ischenko.roman.focustimer.presentation.FocusTimerFragment
import ru.ischenko.roman.focustimer.presentation.SetupPomodoroDialogFragment
import ru.ischenko.roman.focustimer.reports.agenda.di.AgendaModule
import ru.ischenko.roman.focustimer.reports.agenda.presentation.AgendaFragment

@Module
interface ContributesInjectorModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [FocusTimerModule::class])
    fun contributeFocusTimerFragment(): FocusTimerFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [FocusTimerModule::class])
    fun contributeSetupPomodoroDialogFragment(): SetupPomodoroDialogFragment

    @ServiceScope
    @ContributesAndroidInjector
    fun contributeFocusTimerNotificationService(): FocusTimerNotificationService

    @FragmentScope
    @ContributesAndroidInjector(modules = [AgendaModule::class])
    fun contributeAgendaFragment(): AgendaFragment
}