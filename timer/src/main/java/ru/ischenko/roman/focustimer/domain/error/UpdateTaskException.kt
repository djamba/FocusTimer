package ru.ischenko.roman.focustimer.domain.error

import ru.ischenko.roman.focustimer.error.ApplicationLevelException

class UpdateTaskException(override val message: String?, override val cause: Throwable?):
        ApplicationLevelException(message, cause)