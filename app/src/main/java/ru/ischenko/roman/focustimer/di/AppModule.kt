package ru.ischenko.roman.focustimer.di

import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.FocusTimerActivity
import ru.ischenko.roman.focustimer.FocusTimerApplication
import ru.ischenko.roman.focustimer.data.datasource.local.FocusTimerDatabase
import ru.ischenko.roman.focustimer.di.qualifier.AppContext
import ru.ischenko.roman.focustimer.utils.ResourceProvider
import ru.ischenko.roman.focustimer.utils.ResourceProviderImpl

@Module
class AppModule {

    @Provides
    @AppContext
    fun provideContext(application: FocusTimerApplication): Context =
            application.applicationContext

    @Provides
    fun provideContentIntent(@AppContext context: Context): Intent =
            Intent(context, FocusTimerActivity::class.java)

    @Provides
    fun provideResourceProvider(@AppContext context: Context) : ResourceProvider =
            ResourceProviderImpl(context)

    @Provides
    fun provideFocusTimerDatabase(application: FocusTimerApplication): FocusTimerDatabase =
            application.focusTimerDatabase
}