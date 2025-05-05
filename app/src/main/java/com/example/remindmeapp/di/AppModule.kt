package com.example.remindmeapp.di

import com.example.remindmeapp.service.FirebaseAuthServiceInterface
import com.example.remindmeapp.service.FirebaseAuthServiceImpl
import com.example.remindmeapp.service.ReminderService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuthService(): FirebaseAuthServiceInterface {
        return FirebaseAuthServiceImpl()
    }

    @Provides
    @Singleton
    fun provideReminderService(): ReminderService {
        return ReminderService()
    }
}
