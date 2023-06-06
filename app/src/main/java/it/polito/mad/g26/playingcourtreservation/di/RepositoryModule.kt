package it.polito.mad.g26.playingcourtreservation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.polito.mad.g26.playingcourtreservation.repository.NotificationRepository
import it.polito.mad.g26.playingcourtreservation.repository.ReservationRepository
import it.polito.mad.g26.playingcourtreservation.repository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.repository.UserRepository
import it.polito.mad.g26.playingcourtreservation.repository.impl.NotificationRepositoryImpl
import it.polito.mad.g26.playingcourtreservation.repository.impl.ReservationRepositoryImpl
import it.polito.mad.g26.playingcourtreservation.repository.impl.SportCenterRepositoryImpl
import it.polito.mad.g26.playingcourtreservation.repository.impl.UserRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl

    @Singleton
    @Provides
    fun provideSportCenterRepository(impl: SportCenterRepositoryImpl): SportCenterRepository = impl

    @Singleton
    @Provides
    fun provideReservationRepository(impl: ReservationRepositoryImpl): ReservationRepository = impl

    @Singleton
    @Provides
    fun provideNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository =
        impl

}
