package ru.ischenko.roman.focustimer.reports.agenda.domain.error

import ru.ischenko.roman.focustimer.error.ApplicationLevelException

class GetPomodorosInPeriodException(override val message: String?, override val cause: Throwable?):
        ApplicationLevelException(message, cause)