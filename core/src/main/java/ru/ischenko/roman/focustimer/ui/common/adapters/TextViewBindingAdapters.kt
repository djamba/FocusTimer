package ru.ischenko.roman.focustimer.ui.common.adapters

import android.text.format.DateFormat
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText
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

    @JvmStatic
    @BindingAdapter(value = ["android:text", "android:textAttrChanged"], requireAll = false)
    fun updateIntValueBindingAdapter(textView: TextInputEditText, value: Int, attrChanged: InverseBindingListener) {
        if (value != 0 && value.toString() != textView.text.toString()) {
            textView.setText(value.toString())
        }
        textView.doAfterTextChanged { attrChanged.onChange() }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text")
    fun updateIntValueBindingAdapter(view: TextInputEditText): Int {
        return if (view.text.isNullOrEmpty()) {
            0
        } else {
            view.text.toString().toInt()
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["android:text", "android:textAttrChanged"], requireAll = false)
    fun updateLongValueBindingAdapter(textView: TextInputEditText, value: Long, attrChanged: InverseBindingListener) {
        if (value != 0L && value.toString() != textView.text.toString()) {
            textView.setText(value.toString())
        }
        textView.doAfterTextChanged { attrChanged.onChange() }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "android:text")
    fun updateLongValueBindingAdapter(view: TextInputEditText): Long {
        return if (view.text.isNullOrEmpty()) {
            0
        } else {
            view.text.toString().toLong()
        }
    }
}
