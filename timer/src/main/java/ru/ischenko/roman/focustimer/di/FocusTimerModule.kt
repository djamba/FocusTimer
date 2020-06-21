package ru.ischenko.roman.focustimer.di

import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.data.repository.PomodoroRepository
import ru.ischenko.roman.focustimer.data.repository.TaskRepository
import ru.ischenko.roman.focustimer.di.qualifier.AppContext
import ru.ischenko.roman.focustimer.di.qualifier.ViewModelForFactoryInject
import ru.ischenko.roman.focustimer.di.scope.FragmentScope
import ru.ischenko.roman.focustimer.domain.*
import ru.ischenko.roman.focustimer.notification.*
import ru.ischenko.roman.focustimer.presentation.FocusTimerViewModel
import ru.ischenko.roman.focustimer.settings.di.SettingsModule
import ru.ischenko.roman.focustimer.settings.domain.GetPomodoreDefaultEstimateUseCase
import ru.ischenko.roman.focustimer.settings.domain.GetPomodoreRestTimeUseCase
import ru.ischenko.roman.focustimer.settings.domain.GetPomodoreTimeUseCase
import ru.ischenko.roman.focustimer.utils.ResourceProvider
import javax.inject.Provider

@Module(includes = [SettingsModule::class])
class FocusTimerModule {

    @Provides
    fun provideFocusTimerNotification(@AppContext context: Context, contentIntent: Intent) : FocusTimerNotification =
            FocusTimerNotificationImpl(context, contentIntent)

    @Provides
    fun provideFocusTimerController(@AppContext context: Context) : FocusTimerController =
            FocusTimerServiceController(context)

    @Provides
    fun provideFocusTimerServiceMediator(@AppContext context: Context) : FocusTimerServiceMediator =
            FocusTimerServiceMediatorImpl(context)

    @Provides
    fun provideIncreaseSpendPomodoroInTaskUseCase(taskRepository: TaskRepository) : IncreaseSpendPomodoroInTaskUseCase =
            IncreaseSpendPomodoroInTaskUseCase(taskRepository)

    @Provides
    fun provideCreateFreeTaskUseCase(taskRepository: TaskRepository) : CreateFreeTaskUseCase =
            CreateFreeTaskUseCase(taskRepository)

    @Provides
    fun provideUpdateTaskGoalUseCase(taskRepository: TaskRepository) : UpdateTaskGoalUseCase =
            UpdateTaskGoalUseCase(taskRepository)

    @Provides
    fun provideCreatePomodoroUseCase(pomodoroRepository: PomodoroRepository) : CreatePomodoroUseCase =
            CreatePomodoroUseCase(pomodoroRepository)

    @Provides
    fun provideGetTodayPomodorosCountUseCase(pomodoroRepository: PomodoroRepository) : GetTodayPomodorosCountUseCase =
            GetTodayPomodorosCountUseCase(pomodoroRepository)

    @Provides
    @ViewModelForFactoryInject
    fun provideFocusTimerViewModel(focusTimerController: FocusTimerController,
                                   notification: FocusTimerNotification,
                                   focusTimerServiceMediator: FocusTimerServiceMediator,
                                   resourceProvider: ResourceProvider,
                                   createPomodoroUseCase: CreatePomodoroUseCase,
                                   getTodayPomodorosCountUseCase: GetTodayPomodorosCountUseCase,
                                   createFreeTaskUseCase: CreateFreeTaskUseCase,
                                   increaseSpendPomodoroInTaskUseCase: IncreaseSpendPomodoroInTaskUseCase,
                                   updateTaskGoalUseCase: UpdateTaskGoalUseCase,
                                   getPomodoreTimeUseCase: GetPomodoreTimeUseCase,
                                   getPomodoreRestTimeUseCase: GetPomodoreRestTimeUseCase,
                                   getPomodoreDefaultEstimateUseCase: GetPomodoreDefaultEstimateUseCase
    ): FocusTimerViewModel =
            FocusTimerViewModel(focusTimerController, notification, focusTimerServiceMediator, resourceProvider,
                    createPomodoroUseCase, getTodayPomodorosCountUseCase, createFreeTaskUseCase,
                    increaseSpendPomodoroInTaskUseCase, updateTaskGoalUseCase,
                    getPomodoreTimeUseCase, getPomodoreRestTimeUseCase, getPomodoreDefaultEstimateUseCase)

    @Provides
    @FragmentScope
    fun provideFocusTimerViewModelFactory(
            @ViewModelForFactoryInject focusTimerProvider: Provider<FocusTimerViewModel>): ViewModelFactory<FocusTimerViewModel> =
                ViewModelFactory(focusTimerProvider)
}