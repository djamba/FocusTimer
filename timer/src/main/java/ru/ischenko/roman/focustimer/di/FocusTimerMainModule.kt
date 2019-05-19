package ru.ischenko.roman.focustimer.di

import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.di.qualifier.AppContext
import ru.ischenko.roman.focustimer.di.qualifier.ViewModelForFactoryInject
import ru.ischenko.roman.focustimer.di.scope.FragmentScope
import ru.ischenko.roman.focustimer.domain.CreateFreeTaskUseCase
import ru.ischenko.roman.focustimer.domain.CreatePomodoroUseCase
import ru.ischenko.roman.focustimer.domain.UpdateTaskGoalUseCase
import ru.ischenko.roman.focustimer.notification.*
import ru.ischenko.roman.focustimer.presentation.FocusTimerViewModel
import ru.ischenko.roman.focustimer.utils.ResourceProvider
import ru.ischenko.roman.focustimer.utils.ResourceProviderImpl
import javax.inject.Provider

@Module
class FocusTimerMainModule {

    @Provides
    fun provideFocusTimerNotification(@AppContext context: Context, contentIntent: Intent) : FocusTimerNotification =
            FocusTimerNotificationImpl(context, contentIntent)

    @Provides
    fun provideResourceProvider(@AppContext context: Context) : ResourceProvider =
            ResourceProviderImpl(context)

    @Provides
    fun provideFocusTimerServiceController(@AppContext context: Context) : FocusTimerServiceController =
            FocusTimerServiceControllerIml(context)

    @Provides
    fun provideNotificationServiceDelegate(@AppContext context: Context) : NotificationServiceDelegate =
            NotificationServiceDelegateImpl(context)

    @Provides
    fun provideCreateFreeTaskUseCase() : CreateFreeTaskUseCase =
            CreateFreeTaskUseCase()

    @Provides
    fun provideUpdateTaskGoalUseCase() : UpdateTaskGoalUseCase =
            UpdateTaskGoalUseCase()

    @Provides
    fun provideCreatePomodoroUseCase() : CreatePomodoroUseCase =
            CreatePomodoroUseCase()

    @Provides
    @ViewModelForFactoryInject
    fun provideFocusTimerViewModel(focusTimerServiceController: FocusTimerServiceController,
                                   notification: FocusTimerNotification,
                                   notificationServiceDelegate: NotificationServiceDelegate,
                                   resourceProvider: ResourceProvider,
                                   createPomodoroUseCase: CreatePomodoroUseCase,
                                   createFreeTaskUseCase: CreateFreeTaskUseCase,
                                   updateTaskGoalUseCase: UpdateTaskGoalUseCase): FocusTimerViewModel =
            FocusTimerViewModel(focusTimerServiceController, notification, notificationServiceDelegate, resourceProvider,
                    createPomodoroUseCase, createFreeTaskUseCase, updateTaskGoalUseCase)

    @Provides
    @FragmentScope
    fun provideFocusTimerViewModelFactory(
            @ViewModelForFactoryInject focusTimerProvider: Provider<FocusTimerViewModel>): ViewModelFactory<FocusTimerViewModel> =
                ViewModelFactory(focusTimerProvider)
}