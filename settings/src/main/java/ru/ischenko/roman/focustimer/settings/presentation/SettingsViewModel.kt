package ru.ischenko.roman.focustimer.settings.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.ischenko.roman.focustimer.settings.domain.*

class SettingsViewModel(
        private val getPomodoreTimeUseCase: GetPomodoreTimeUseCase,
        private val putPomodoreTimeUseCase: PutPomodoreTimeUseCase,
        private val getPomodoreRestTimeUseCase: GetPomodoreRestTimeUseCase,
        private val putPomodoreRestTimeUseCase: PutPomodoreRestTimeUseCase,
        private val getPomodoreDefaultEstimateUseCase: GetPomodoreDefaultEstimateUseCase,
        private val putPomodoreDefaultEstimateUseCase: PutPomodoreDefaultEstimateUseCase
) : ViewModel() {

    val pomodoreTime: MutableLiveData<Long> = MutableLiveData()
    val restTime: MutableLiveData<Long> = MutableLiveData()
    val defaultTaskEstimate: MutableLiveData<Int> = MutableLiveData()

    init {
        pomodoreTime.value = getPomodoreTimeUseCase()
        restTime.value = getPomodoreRestTimeUseCase()
        defaultTaskEstimate.value = getPomodoreDefaultEstimateUseCase()
    }

    override fun onCleared() {
        saveSettings()
        super.onCleared()
    }

    private fun saveSettings() {
        pomodoreTime.value?.let {
            putPomodoreTimeUseCase(it)
        }
        restTime.value?.let {
            putPomodoreRestTimeUseCase(it)
        }
        defaultTaskEstimate.value?.let {
            putPomodoreDefaultEstimateUseCase(it)
        }
    }
}
