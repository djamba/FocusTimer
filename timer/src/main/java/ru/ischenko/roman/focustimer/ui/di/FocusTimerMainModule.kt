package ru.ischenko.roman.focustimer.ui.di

import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import ru.ischenko.roman.focustimer.di.ViewModelFactory
import ru.ischenko.roman.focustimer.di.qualifier.AppContext
import ru.ischenko.roman.focustimer.di.qualifier.ViewModelForFactoryInject
import ru.ischenko.roman.focustimer.di.scope.FragmentScope
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
    fun provideFocusTimerServiceController(@AppContext context: Context) : FocusTimerServiceController =
            FocusTimerServiceControllerIml(context)

    @Provides
    fun provideNotificationServiceDelegate(@AppContext context: Context) : NotificationServiceDelegate =
            NotificationServiceDelegateImpl(context)

    @Provides
    @ViewModelForFactoryInject
    fun provideFocusTimerViewModel(focusTimerServiceController: FocusTimerServiceController,
                                   notification: FocusTimerNotification,
                                   notificationServiceDelegate: NotificationServiceDelegate,
                                   resourceProvider: ResourceProvider): FocusTimerViewModel =
            FocusTimerViewModel(focusTimerServiceController, notification, notificationServiceDelegate, resourceProvider)

    @Provides
    @FragmentScope
    fun provideFocusTimerViewModelFactory(
            @ViewModelForFactoryInject focusTimerProvider: Provider<FocusTimerViewModel>): ViewModelFactory<FocusTimerViewModel> =
                ViewModelFactory(focusTimerProvider)
}