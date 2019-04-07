package ru.ischenko.roman.focustimer.ui.main.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.ui.di.AppContext
import ru.ischenko.roman.focustimer.ui.di.FragmentScope
import ru.ischenko.roman.focustimer.ui.main.FocusTimerNotification
import ru.ischenko.roman.focustimer.ui.main.FocusTimerViewModel
import ru.ischenko.roman.focustimer.ui.main.NotificationServiceDelegate
import ru.ischenko.roman.focustimer.ui.notification.*
import ru.ischenko.roman.focustimer.utils.ui.ResourceProvider
import ru.ischenko.roman.focustimer.utils.ui.ResourceProviderImpl
import ru.ischenko.roman.focustimer.utils.ui.ViewModelFactory
import javax.inject.Provider

@Module
class FocusTimerMainModule {

    @Provides
    fun provideFocusTimerNotification(@AppContext context: Context) : FocusTimerNotification =
            FocusTimerNotificationImpl(context)

    @Provides
    fun provideResourceProvider(@AppContext context: Context) : ResourceProvider =
            ResourceProviderImpl(context)

    @Provides
    fun provideStartTimerUseCase(@AppContext context: Context) : StartTimerUseCase =
            StartTimerUseCase(context)

    @Provides
    fun provideStopTimerUseCase(@AppContext context: Context) : StopTimerUseCase =
            StopTimerUseCase(context)

    @Provides
    fun provideResumePauseTimerUseCase(@AppContext context: Context) : ResumePauseTimerUseCase =
            ResumePauseTimerUseCase(context)

    @Provides
    fun provideNotificationServiceDelegate(@AppContext context: Context) : NotificationServiceDelegate =
            NotificationServiceDelegateImpl(context)

    @Provides
    @FragmentScope
    fun provideFocusTimerViewModel(startTimerUseCase: StartTimerUseCase,
                                   stopTimerUseCase: StopTimerUseCase,
                                   resumePauseTimerUseCase: ResumePauseTimerUseCase,
                                   notification: FocusTimerNotification,
                                   notificationServiceDelegate: NotificationServiceDelegate,
                                   resourceProvider: ResourceProvider): FocusTimerViewModel =
            FocusTimerViewModel(startTimerUseCase, stopTimerUseCase, resumePauseTimerUseCase,
                                notification, notificationServiceDelegate, resourceProvider)

    @Provides
    @FragmentScope
    fun provideFocusTimerViewModelFactory(
                    focusTimerProvider: Provider<FocusTimerViewModel>): ViewModelFactory<FocusTimerViewModel> =
            ViewModelFactory(focusTimerProvider)
}