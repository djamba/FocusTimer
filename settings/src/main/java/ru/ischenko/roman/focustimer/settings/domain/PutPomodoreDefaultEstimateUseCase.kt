package ru.ischenko.roman.focustimer.settings.domain

import ru.ischenko.roman.focustimer.settings.domain.repository.SettingsRepository

class PutPomodoreDefaultEstimateUseCase(private val settingsRepository: SettingsRepository) {

    operator fun invoke(pomodoreCount: Int) =
            settingsRepository.putPomodoreDefaultEstimate(pomodoreCount)
}
