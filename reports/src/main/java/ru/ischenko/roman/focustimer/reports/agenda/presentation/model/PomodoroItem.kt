package ru.ischenko.roman.focustimer.reports.agenda.presentation.model

import java.util.*

data class PomodoroItem(val startDate: Date, val duration: Long, val goal: String)