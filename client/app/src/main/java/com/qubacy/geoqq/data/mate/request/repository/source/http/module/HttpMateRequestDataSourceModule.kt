package com.qubacy.geoqq.data.mate.request.repository.source.http.module

import android.content.Context
import com.qubacy.geoqq.data.mate.request.repository.source.http.HttpMateRequestDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpMateRequestDataSourceModule {
    @Provides
    fun provideHttpMateRequestDataSource(
        @ApplicationContext context: Context
    ): HttpMateRequestDataSource {
        val httpApi = (context as CustomApplication).httpApi

        return httpApi.mateRequestApi
    }
}