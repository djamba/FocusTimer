package ru.ischenko.roman.focustimer.settings.data.repository.datasource

interface SettingsPreferences {
    fun getPomodoreTime(): Long
    fun putPomodoreTime(timeMin: Long)
    fun getPomodoreRestTime(): Long
    fun putPomodoreRestTime(timeMin: Long)
    fun getPomodoreDefaultEstimate(): Int
    fun putPomodoreDefaultEstimate(estimate: Int)
}