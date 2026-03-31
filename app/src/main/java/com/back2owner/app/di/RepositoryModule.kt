package com.back2owner.app.di

import com.back2owner.app.data.repository.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun provideItemRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
    ): ItemRepository = FirebaseItemRepository(firestore, storage)

    @Singleton
    @Provides
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
    ): UserRepository = FirebaseUserRepository(firestore, auth)

    @Singleton
    @Provides
    fun provideClaimRepository(
        firestore: FirebaseFirestore,
    ): ClaimRepository = FirebaseClaimRepository(firestore)

    @Singleton
    @Provides
    fun provideNotificationRepository(
        firestore: FirebaseFirestore,
    ): NotificationRepository = FirebaseNotificationRepository(firestore)

    @Singleton
    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
    ): AuthRepository = FirebaseAuthRepository(auth, firestore)
}
