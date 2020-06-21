package ru.ischenko.roman.focustimer.settings.domain

import ru.ischenko.roman.focustimer.settings.domain.repository.SettingsRepository

class GetPomodoreRestTimeUseCase(private val settingsRepository: SettingsRepository) {

    operator fun invoke(): Long =
            settingsRepository.getPomodoreRestTime()
}
