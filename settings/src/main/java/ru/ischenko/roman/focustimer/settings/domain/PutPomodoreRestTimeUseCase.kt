package ru.ischenko.roman.focustimer.settings.domain

import ru.ischenko.roman.focustimer.settings.domain.repository.SettingsRepository

class PutPomodoreRestTimeUseCase(private val settingsRepository: SettingsRepository) {

    operator fun invoke(timeMin: Long) =
            settingsRepository.putPomodoreRestTime(timeMin)
}
