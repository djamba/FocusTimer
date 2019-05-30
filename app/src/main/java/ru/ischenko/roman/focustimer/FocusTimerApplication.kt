package ru.ischenko.roman.focustimer

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import ru.ischenko.roman.focustimer.data.datasource.local.FocusTimerDatabase
import ru.ischenko.roman.focustimer.di.DaggerAppComponent
import timber.log.Timber

/**
 * User: roman
 * Date: 07.04.19
 * Time: 20:04
 */
class FocusTimerApplication : DaggerApplication() {

    lateinit var focusTimerDatabase: FocusTimerDatabase

    override fun onCreate() {
        super.onCreate()

        // TODO: Настроить для релиза
        Timber.plant(Timber.DebugTree())

        focusTimerDatabase = FocusTimerDatabase.buildDatabase(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}