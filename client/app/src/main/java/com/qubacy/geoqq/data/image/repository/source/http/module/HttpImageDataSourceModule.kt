package com.qubacy.geoqq.data.image.repository.source.http.module

import android.content.Context
import com.qubacy.geoqq.data.image.repository.source.http.HttpImageDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpImageDataSourceModule {
    @Provides
    fun provideHttpImageDataSource(
        @ApplicationContext context: Context
    ): HttpImageDataSource {
        val httpApi = (context as CustomApplication).httpApi

        return httpApi.imageApi
    }
}