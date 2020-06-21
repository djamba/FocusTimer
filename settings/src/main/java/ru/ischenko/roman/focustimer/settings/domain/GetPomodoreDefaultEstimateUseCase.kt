package ru.ischenko.roman.focustimer.settings.domain

import ru.ischenko.roman.focustimer.settings.domain.repository.SettingsRepository

class GetPomodoreDefaultEstimateUseCase(private val settingsRepository: SettingsRepository) {

    operator fun invoke() =
            settingsRepository.getPomodoreDefaultEstimate()
}
