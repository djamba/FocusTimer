package ru.ischenko.roman.focustimer.notification

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

/**
 * User: roman
 * Date: 31.03.19
 * Time: 18:41
 */
class NotificationServiceDelegateImpl(private val context: Context) : NotificationServiceDelegate {

    private var serviceBound = false
    private var focusTimerNotificationService: FocusTimerNotificationService? = null
    private var onTimeChangedListener: OnTimeChangedListener? = null

    override fun startService(onTimeChangedListener: OnTimeChangedListener) {
        this.onTimeChangedListener = onTimeChangedListener
        val intent = Intent(context, FocusTimerNotificationService::class.java)
        context.applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun stopService() {
        if (serviceBound) {
            focusTimerNotificationService?.onTimeChangedListener = null
            context.applicationContext.unbindService(serviceConnection)
            serviceBound = false
        }
    }

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            val notificationServiceBinder = binder as FocusTimerNotificationService.NotificationServiceBinder
            focusTimerNotificationService = notificationServiceBinder.service
            focusTimerNotificationService?.onTimeChangedListener = onTimeChangedListener
            serviceBound = true
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            focusTimerNotificationService?.onTimeChangedListener = null
            serviceBound = false
        }
    }
}