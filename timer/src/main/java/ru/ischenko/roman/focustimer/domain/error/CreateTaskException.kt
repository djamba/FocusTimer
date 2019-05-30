package ru.ischenko.roman.focustimer.domain.error

import ru.ischenko.roman.focustimer.error.ApplicationLevelException

class CreateTaskException(override val message: String?, override val cause: Throwable?):
        ApplicationLevelException(message, cause)