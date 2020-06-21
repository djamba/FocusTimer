package ru.ischenko.roman.focustimer.settings.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.di.ViewModelFactory
import ru.ischenko.roman.focustimer.di.qualifier.AppContext
import ru.ischenko.roman.focustimer.di.qualifier.ViewModelForFactoryInject
import ru.ischenko.roman.focustimer.di.scope.FragmentScope
import ru.ischenko.roman.focustimer.settings.data.datasource.SettingsPreferencesImpl
import ru.ischenko.roman.focustimer.settings.data.repository.SettingsRepositoryIml
import ru.ischenko.roman.focustimer.settings.data.repository.datasource.SettingsPreferences
import ru.ischenko.roman.focustimer.settings.domain.*
import ru.ischenko.roman.focustimer.settings.domain.repository.SettingsRepository
import ru.ischenko.roman.focustimer.settings.presentation.SettingsViewModel
import javax.inject.Provider

@Module
class SettingsModule {

    @Provides
    @FragmentScope
    fun provideSettingsPreferences(@AppContext context: Context): SettingsPreferences =
            SettingsPreferencesImpl(context)

    @Provides
    @FragmentScope
    fun provideSettingsRepository(settingsPreferences: SettingsPreferences): SettingsRepository =
            SettingsRepositoryIml(settingsPreferences)

    @Provides
    @FragmentScope
    fun provideGetPomodoreTime(settingsRepository: SettingsRepository) =
            GetPomodoreTimeUseCase(settingsRepository)

    @Provides
    @FragmentScope
    fun providePutPomodoreTimeUseCase(settingsRepository: SettingsRepository) =
            PutPomodoreTimeUseCase(settingsRepository)

    @Provides
    @FragmentScope
    fun provideGetPomodoreRestTimeUseCase(settingsRepository: SettingsRepository) =
            GetPomodoreRestTimeUseCase(settingsRepository)

    @Provides
    @FragmentScope
    fun providePutPomodoreRestTimeUseCase(settingsRepository: SettingsRepository) =
            PutPomodoreRestTimeUseCase(settingsRepository)

    @Provides
    @FragmentScope
    fun provideGetPomodoreDefaultEstimateUseCasee(settingsRepository: SettingsRepository) =
            GetPomodoreDefaultEstimateUseCase(settingsRepository)

    @Provides
    @FragmentScope
    fun providePutPomodoreDefaultEstimateUseCase(settingsRepository: SettingsRepository) =
            PutPomodoreDefaultEstimateUseCase(settingsRepository)

    @Provides
    @ViewModelForFactoryInject
    fun provideAgendaViewModel(
            getPomodoreTime: GetPomodoreTimeUseCase,
            putPomodoreTime: PutPomodoreTimeUseCase,
            getPomodoreRestTime: GetPomodoreRestTimeUseCase,
            putPomodoreRestTime: PutPomodoreRestTimeUseCase,
            getPomodoreDefaultEstimate: GetPomodoreDefaultEstimateUseCase,
            putPomodoreDefaultEstimate: PutPomodoreDefaultEstimateUseCase
    ): SettingsViewModel =
            SettingsViewModel(
                    getPomodoreTime,
                    putPomodoreTime,
                    getPomodoreRestTime,
                    putPomodoreRestTime,
                    getPomodoreDefaultEstimate,
                    putPomodoreDefaultEstimate
            )

    @Provides
    @FragmentScope
    fun provideSettingsViewModelFactory(
            @ViewModelForFactoryInject focusTimerProvider: Provider<SettingsViewModel>
    ): ViewModelFactory<SettingsViewModel> =
            ViewModelFactory(focusTimerProvider)
}
