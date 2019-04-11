package ru.ischenko.roman.focustimer

import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import ru.ischenko.roman.focustimer.di.DaggerAppComponent

/**
 * User: roman
 * Date: 07.04.19
 * Time: 20:04
 */
class FocusTimerApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}