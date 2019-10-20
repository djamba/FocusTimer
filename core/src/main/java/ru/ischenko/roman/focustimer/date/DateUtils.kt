package ru.ischenko.roman.focustimer.date

import java.util.*

fun Date.beginTime(): Date {
    val cal = Calendar.getInstance().apply {
        time = this@beginTime
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    return cal.time
}

fun Date.endTime(): Date {
    val cal = Calendar.getInstance().apply {
        time = this@endTime
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
    }
    return cal.time
}

fun Date.nextDay(): Date {
    val cal = Calendar.getInstance().apply {
        time = this@nextDay
        add(Calendar.DATE, 1)
    }
    return cal.time
}

fun Date.prevDay(): Date {
    val cal = Calendar.getInstance().apply {
        time = this@prevDay
        add(Calendar.DATE, -1)
    }
    return cal.time
}

fun getBeginToday(): Date {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }
    return cal.time
}

fun getEndToday(): Date {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
    }
    return cal.time
}