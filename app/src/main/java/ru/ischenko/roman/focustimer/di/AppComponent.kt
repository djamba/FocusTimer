package ru.ischenko.roman.focustimer.di

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.ischenko.roman.focustimer.FocusTimerApplication
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    AndroidSupportInjectionModule::class,
    ContributesInjectorModule::class
])
interface AppComponent : AndroidInjector<FocusTimerApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<FocusTimerApplication>()
}