package ru.ischenko.roman.focustimer.utils.ui

import android.content.Context

/**
 * User: roman
 * Date: 18.03.19
 * Time: 22:34
 */
class ResourceProvider(context: Context) {

    private val appContext: Context = context.applicationContext

    fun getText(resId: Int): String {
        return appContext.getString(resId)
    }
}