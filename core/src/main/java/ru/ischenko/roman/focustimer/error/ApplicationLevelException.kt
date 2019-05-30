package ru.ischenko.roman.focustimer.error

open class ApplicationLevelException(override val message: String?,
                                     override val cause: Throwable? = null) : Exception(message, cause)