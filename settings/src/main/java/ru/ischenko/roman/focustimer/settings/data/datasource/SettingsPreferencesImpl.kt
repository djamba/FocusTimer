package ru.ischenko.roman.focustimer.settings.data.datasource

import android.content.Context
import android.content.SharedPreferences
import ru.ischenko.roman.focustimer.settings.data.repository.datasource.SettingsPreferences

private const val SHARED_PREF_NAME = "POMODORE_SETTINGS_PREF"
private const val POMODORE_TIME_KEY = "POMODORE_TIME_KEY"
private const val POMODORE_REST_TIME_KEY = "POMODORE_REST_TIME_KEY"
private const val POMODORE_TASK_ESTIMATE_KEY = "POMODORE_TASK_ESTIMATE_KEY"

private const val DEFAULT_POMODORE_TIME = 25L
private const val DEFAULT_REST_TIME = 5L
private const val DEFAULT_TASK_ESTIMATE = 4

internal class SettingsPreferencesImpl(context: Context) : SettingsPreferences {

    private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    override fun getPomodoreTime() =
        sharedPreferences.getLong(POMODORE_TIME_KEY, DEFAULT_POMODORE_TIME)

    override fun putPomodoreTime(timeMin: Long) =
        sharedPreferences.edit().putLong(POMODORE_TIME_KEY, timeMin).apply()

    override fun getPomodoreRestTime() =
            sharedPreferences.getLong(POMODORE_REST_TIME_KEY, DEFAULT_REST_TIME)

    override fun putPomodoreRestTime(timeMin: Long) =
        sharedPreferences.edit().putLong(POMODORE_REST_TIME_KEY, timeMin).apply()

    override fun getPomodoreDefaultEstimate() =
            sharedPreferences.getInt(POMODORE_TASK_ESTIMATE_KEY, DEFAULT_TASK_ESTIMATE)

    override fun putPomodoreDefaultEstimate(estimate: Int) =
            sharedPreferences.edit().putInt(POMODORE_TASK_ESTIMATE_KEY, estimate).apply()
}
