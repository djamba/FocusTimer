package ru.ischenko.roman.focustimer.ui.utils

import android.content.Context
import androidx.annotation.StringRes

/**
 * User: roman
 * Date: 18.03.19
 * Time: 22:34
 */

interface ResourceProvider {

    fun getText(@StringRes resId: Int): String
}

class ResourceProviderImpl(context: Context) : ResourceProvider {

    private val appContext: Context = context.applicationContext

    override fun getText(@StringRes resId: Int): String {
        return appContext.getString(resId)
    }
}