package com.jainhardik120.talevista.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.jainhardik120.talevista.data.remote.PostsApi
import com.jainhardik120.talevista.data.remote.TaleVistaApi
import com.jainhardik120.talevista.data.remote.TokenInterceptor
import com.jainhardik120.talevista.data.remote.UsersApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun api(): TaleVistaApi =
        Retrofit.Builder().addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create()).baseUrl(
                TaleVistaApi.BASE_URL
            ).build().create()


    @Provides
    fun provideSharedPreferences(app: Application): SharedPreferences {
        return app.getSharedPreferences(
            "com.jainhardik120.talevista.preferences",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    fun postsApi(tokenInterceptor : TokenInterceptor): PostsApi {

        return Retrofit.Builder().addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().apply {
                addInterceptor(tokenInterceptor)
            }.build())
            .baseUrl(
                PostsApi.BASE_URL
            ).build().create()
    }

    @Provides
    fun usersApi(tokenInterceptor: TokenInterceptor): UsersApi {

        return Retrofit.Builder().addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(OkHttpClient.Builder().apply {
                addInterceptor(tokenInterceptor)
            }.build())
            .baseUrl(
                UsersApi.BASE_URL
            ).build().create()
    }

    @Provides
    fun provideTokenInterceptor(sharedPreferences: SharedPreferences): TokenInterceptor {
        return TokenInterceptor(sharedPreferences)
    }
}