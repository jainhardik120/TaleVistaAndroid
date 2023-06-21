package com.jainhardik120.talevista.di

import com.jainhardik120.talevista.data.repository.AuthControllerImpl
import com.jainhardik120.talevista.domain.repository.AuthController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthController(authControllerImpl: AuthControllerImpl) : AuthController
}