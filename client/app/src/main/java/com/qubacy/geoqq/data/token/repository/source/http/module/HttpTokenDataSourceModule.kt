package com.qubacy.geoqq.data.token.repository.source.http.module

import android.content.Context
import com.qubacy.geoqq.data.token.repository.source.http.HttpTokenDataSource
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
    ): HttpTokenDataSource {
        val httpApi = (context as CustomApplication).httpApi

        return httpApi.tokenApi
    }
}