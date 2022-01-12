package com.android.stores_app.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import com.android.stores_app.BuildConfig
import com.android.stores_app.common.util.Constants.TIMEOUT_REQUEST
import com.android.stores_app.common.helper.UiHelper
import com.android.stores_app.data.api.StoresService
import com.android.stores_app.domain.StoresRepository
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesRetrofitBuilder(httpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .build()

    @Provides
    fun provideStoresService(retrofit: Retrofit): StoresService =
        retrofit.create(StoresService::class.java)

    @Provides
    fun storesRepositoryProvider(): StoresRepository =
        StoresRepository(provideStoresService(providesRetrofitBuilder(httpClient())))

    @Provides
    fun httpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_REQUEST, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_REQUEST, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_REQUEST, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun connectivityManager(application: Application): ConnectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    fun uiHelper(application: Application): UiHelper = UiHelper(application,connectivityManager(application))

    @Provides
    @Singleton
    fun locationProviderClient(application: Application) =
        LocationServices.getFusedLocationProviderClient(application)
}