package ru.ischenko.roman.focustimer.ui.main

import ru.ischenko.roman.focustimer.ui.notification.OnTimeChangedListener

/**
 * User: roman
 * Date: 31.03.19
 * Time: 19:17
 */
interface NotificationServiceDelegate {

    fun startService(onTimeChangedListener: OnTimeChangedListener)

    fun stopService()
}