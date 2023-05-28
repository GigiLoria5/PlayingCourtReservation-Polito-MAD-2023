package it.polito.mad.g26.playingcourtreservation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.UserRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.impl.SportCenterRepositoryImpl
import it.polito.mad.g26.playingcourtreservation.newRepository.impl.UserRepositoryImpl
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

}
