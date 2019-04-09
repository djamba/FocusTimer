package ru.ischenko.roman.focustimer.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.ischenko.roman.focustimer.ui.di.FragmentScope
import ru.ischenko.roman.focustimer.ui.main.FocusTimerFragment
import ru.ischenko.roman.focustimer.ui.main.di.FocusTimerMainModule

@Module
interface ContributesInjectorModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [FocusTimerMainModule::class])
    fun contributeFocusTimerFragment(): FocusTimerFragment
}