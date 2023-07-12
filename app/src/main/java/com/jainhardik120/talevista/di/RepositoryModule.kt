package com.jainhardik120.talevista.di

import com.jainhardik120.talevista.data.repository.AuthControllerImpl
import com.jainhardik120.talevista.data.repository.PostsRepositoryImpl
import com.jainhardik120.talevista.data.repository.UserRepositoryImpl
import com.jainhardik120.talevista.domain.repository.AuthController
import com.jainhardik120.talevista.domain.repository.PostsRepository
import com.jainhardik120.talevista.domain.repository.UserRepository
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
    abstract fun bindAuthController(authControllerImpl: AuthControllerImpl): AuthController

    @Binds
    @Singleton
    abstract fun bindPostsRepository(postsRepositoryImpl: PostsRepositoryImpl): PostsRepository

    @Binds
    @Singleton
    abstract fun bindUsersRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

}