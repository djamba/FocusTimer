package ru.ischenko.roman.focustimer.di

import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.FocusTimerActivity
import ru.ischenko.roman.focustimer.FocusTimerApplication

@Module
class AppModule {

    @Provides
    @AppContext
    fun provideContext(application: FocusTimerApplication): Context =
            application.applicationContext

    @Provides
    fun provideContentIntent(@AppContext context: Context): Intent =
            Intent(context, FocusTimerActivity::class.java)
}