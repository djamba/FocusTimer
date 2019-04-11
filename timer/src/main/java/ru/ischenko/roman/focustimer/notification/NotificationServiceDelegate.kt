package ru.ischenko.roman.focustimer.notification

/**
 * User: roman
 * Date: 31.03.19
 * Time: 19:17
 */
interface NotificationServiceDelegate {

    fun startService(onTimeChangedListener: OnTimeChangedListener)

    fun stopService()
}