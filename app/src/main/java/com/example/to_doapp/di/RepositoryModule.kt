package com.example.to_doapp.di

import com.example.to_doapp.data.repositories.TaskRepository
import com.example.to_doapp.data.repositories.TaskRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(
        firebaseFirestore: FirebaseFirestore,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): TaskRepository {
        return TaskRepositoryImpl(
            todoAppDb = firebaseFirestore,
            ioDispatcher = ioDispatcher
        )
    }
}