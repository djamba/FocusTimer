package ru.ischenko.roman.focustimer.ui.common.adapters

import android.text.format.DateFormat
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.util.*

object TextViewBindingAdapters {

    @JvmStatic
    @BindingAdapter("android:text")
    fun updateTimeBindingAdapter(textView: TextView, date: Date?) {
        if (date == null) {
            return
        }

        val dateFormat = DateFormat.getDateFormat(textView.context.applicationContext)
        textView.text = dateFormat.format(date)
    }
}