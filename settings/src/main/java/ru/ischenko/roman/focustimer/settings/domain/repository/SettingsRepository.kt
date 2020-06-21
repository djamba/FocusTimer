package ru.ischenko.roman.focustimer.settings.domain.repository

interface SettingsRepository {
    fun getPomodoreTime(): Long
    fun putPomodoreTime(timeMin: Long)
    fun getPomodoreRestTime(): Long
    fun putPomodoreRestTime(timeMin: Long)
    fun getPomodoreDefaultEstimate(): Int
    fun putPomodoreDefaultEstimate(estimate: Int)
}
