package ru.ischenko.roman.focustimer.settings.data.repository

import ru.ischenko.roman.focustimer.settings.data.repository.datasource.SettingsPreferences
import ru.ischenko.roman.focustimer.settings.domain.repository.SettingsRepository

internal class SettingsRepositoryIml(private val settingsPreferences: SettingsPreferences) : SettingsRepository {

    override fun getPomodoreTime(): Long =
            settingsPreferences.getPomodoreTime()

    override fun putPomodoreTime(timeMin: Long) =
            settingsPreferences.putPomodoreTime(timeMin)

    override fun getPomodoreRestTime(): Long =
            settingsPreferences.getPomodoreRestTime()

    override fun putPomodoreRestTime(timeMin: Long) =
            settingsPreferences.putPomodoreRestTime(timeMin)

    override fun getPomodoreDefaultEstimate(): Int =
            settingsPreferences.getPomodoreDefaultEstimate()

    override fun putPomodoreDefaultEstimate(estimate: Int) =
            settingsPreferences.putPomodoreDefaultEstimate(estimate)
}
