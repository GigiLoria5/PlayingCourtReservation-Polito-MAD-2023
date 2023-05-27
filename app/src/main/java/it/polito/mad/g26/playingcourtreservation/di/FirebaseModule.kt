package it.polito.mad.g26.playingcourtreservation.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.polito.mad.g26.playingcourtreservation.newRepository.SportCenterRepository
import it.polito.mad.g26.playingcourtreservation.newRepository.impl.SportCenterRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    // Any class that needs these dependencies will get the same instance

    @Singleton
    @Provides
    fun provideFirestoreInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideSportCenterRepository(db: FirebaseFirestore): SportCenterRepository {
        return SportCenterRepositoryImpl(db)
    }
}
