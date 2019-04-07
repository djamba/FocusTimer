package ru.ischenko.roman.focustimer.ui.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.ui.FocusTimerApplication

@Module
class AppModule {

    @Provides
    @AppContext
    fun provideContext(application: FocusTimerApplication): Context {
        return application.applicationContext
    }
}