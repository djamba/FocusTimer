package ru.ischenko.roman.focustimer.reports.agenda.di

import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.data.repository.PomodoroRepository
import ru.ischenko.roman.focustimer.di.ViewModelFactory
import ru.ischenko.roman.focustimer.di.qualifier.ViewModelForFactoryInject
import ru.ischenko.roman.focustimer.di.scope.FragmentScope
import ru.ischenko.roman.focustimer.reports.agenda.domain.GetPomodorosInPeriodUseCase
import ru.ischenko.roman.focustimer.reports.agenda.presentation.AgendaViewModel
import ru.ischenko.roman.focustimer.reports.agenda.presentation.convertor.PomodoroToPomodoroItemConverter
import ru.ischenko.roman.focustimer.utils.ResourceProvider
import javax.inject.Provider

@Module
class AgendaModule {

    @Provides
    fun provideGetPomodorosInPeriodUseCase(pomodoroRepository: PomodoroRepository) : GetPomodorosInPeriodUseCase =
            GetPomodorosInPeriodUseCase(pomodoroRepository)

    @Provides
    fun providePomodoroToPomodoroItemConverter() : PomodoroToPomodoroItemConverter =
            PomodoroToPomodoroItemConverter()

    @Provides
    @ViewModelForFactoryInject
    fun provideAgendaViewModel(getPomodorosInPeriodUseCase: GetPomodorosInPeriodUseCase,
                               resourceProvider: ResourceProvider,
                               pomodoroToPomodoroItemConverter: PomodoroToPomodoroItemConverter): AgendaViewModel =
            AgendaViewModel(getPomodorosInPeriodUseCase, resourceProvider, pomodoroToPomodoroItemConverter)

    @Provides
    @FragmentScope
    fun provideAgendaViewModelFactory(
            @ViewModelForFactoryInject focusTimerProvider: Provider<AgendaViewModel>): ViewModelFactory<AgendaViewModel> =
            ViewModelFactory(focusTimerProvider)
}