package com.qubacy.geoqq.data.auth.repository.source.http.api.module

import android.content.Context
import com.qubacy.geoqq.data.auth.repository.source.http.api.HttpAuthDataSourceApi
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpTokenDataSourceModule {
    @Provides
    fun provideHttpTokenDataSource(
       @ApplicationContext context: Context
    ): HttpAuthDataSourceApi {
        val httpApi = (context as CustomApplication).httpApi

        return httpApi.tokenApi
    }
}