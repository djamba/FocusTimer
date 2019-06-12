package ru.ischenko.roman.focustimer.di

import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.data.datasource.local.FocusTimerDatabase
import ru.ischenko.roman.focustimer.data.repository.PomodoroRepository
import ru.ischenko.roman.focustimer.data.repository.PomodoroRepositoryImpl
import ru.ischenko.roman.focustimer.data.repository.TaskRepository
import ru.ischenko.roman.focustimer.data.repository.TaskRepositoryImpl
import ru.ischenko.roman.focustimer.data.repository.converters.PomodoroConverter
import ru.ischenko.roman.focustimer.data.repository.converters.ProjectConverter
import ru.ischenko.roman.focustimer.data.repository.converters.TaskConverter
import ru.ischenko.roman.focustimer.di.qualifier.AppContext
import ru.ischenko.roman.focustimer.di.qualifier.ViewModelForFactoryInject
import ru.ischenko.roman.focustimer.di.scope.FragmentScope
import ru.ischenko.roman.focustimer.domain.CreateFreeTaskUseCase
import ru.ischenko.roman.focustimer.domain.CreatePomodoroUseCase
import ru.ischenko.roman.focustimer.domain.IncreaseSpendPomodoroInTaskUseCase
import ru.ischenko.roman.focustimer.domain.UpdateTaskGoalUseCase
import ru.ischenko.roman.focustimer.notification.*
import ru.ischenko.roman.focustimer.presentation.FocusTimerViewModel
import ru.ischenko.roman.focustimer.utils.ResourceProvider
import ru.ischenko.roman.focustimer.utils.ResourceProviderImpl
import javax.inject.Provider

@Module
class FocusTimerModule {

    @Provides
    fun provideFocusTimerNotification(@AppContext context: Context, contentIntent: Intent) : FocusTimerNotification =
            FocusTimerNotificationImpl(context, contentIntent)

    @Provides
    fun provideResourceProvider(@AppContext context: Context) : ResourceProvider =
            ResourceProviderImpl(context)

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
    fun provideTaskConverter() : TaskConverter =
            TaskConverter(ProjectConverter())

    @Provides
    fun providePomodoroConverter(taskConverter: TaskConverter) : PomodoroConverter =
            PomodoroConverter(taskConverter)

    @Provides
    fun providePomodoroRepository(database: FocusTimerDatabase,
                                  pomodoroConverter: PomodoroConverter,
                                  taskConverter: TaskConverter) : PomodoroRepository =
            PomodoroRepositoryImpl(database.pomodoroDao(), pomodoroConverter, database.taskDao(), taskConverter)

    @Provides
    fun provideTaskRepository(database: FocusTimerDatabase,
                              taskConverter: TaskConverter) : TaskRepository =
            TaskRepositoryImpl(database.taskDao(), taskConverter)

    @Provides
    fun provideCreatePomodoroUseCase(pomodoroRepository: PomodoroRepository) : CreatePomodoroUseCase =
            CreatePomodoroUseCase(pomodoroRepository)

    @Provides
    @ViewModelForFactoryInject
    fun provideFocusTimerViewModel(focusTimerController: FocusTimerController,
                                   notification: FocusTimerNotification,
                                   focusTimerServiceMediator: FocusTimerServiceMediator,
                                   resourceProvider: ResourceProvider,
                                   createPomodoroUseCase: CreatePomodoroUseCase,
                                   createFreeTaskUseCase: CreateFreeTaskUseCase,
                                   increaseSpendPomodoroInTaskUseCase: IncreaseSpendPomodoroInTaskUseCase,
                                   updateTaskGoalUseCase: UpdateTaskGoalUseCase): FocusTimerViewModel =
            FocusTimerViewModel(focusTimerController, notification, focusTimerServiceMediator, resourceProvider,
                    createPomodoroUseCase, createFreeTaskUseCase, increaseSpendPomodoroInTaskUseCase, updateTaskGoalUseCase)

    @Provides
    @FragmentScope
    fun provideFocusTimerViewModelFactory(
            @ViewModelForFactoryInject focusTimerProvider: Provider<FocusTimerViewModel>): ViewModelFactory<FocusTimerViewModel> =
                ViewModelFactory(focusTimerProvider)
}