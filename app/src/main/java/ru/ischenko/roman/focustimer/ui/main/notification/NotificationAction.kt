package ru.ischenko.roman.focustimer.ui.main.notification

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * User: roman
 * Date: 07.02.19
 * Time: 21:31
 */

sealed class NotificationAction() : Parcelable

@Parcelize
object ResumePauseAction : NotificationAction()

@Parcelize
object CancelAction : NotificationAction()

@Parcelize
data class CustomAction(val action: String, val title: String) : NotificationAction()