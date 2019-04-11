package ru.ischenko.roman.focustimer.ui.di

import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.di.AppContext
import ru.ischenko.roman.focustimer.di.FragmentScope
import ru.ischenko.roman.focustimer.di.ViewModelFactory
import ru.ischenko.roman.focustimer.notification.*
import ru.ischenko.roman.focustimer.ui.FocusTimerViewModel
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