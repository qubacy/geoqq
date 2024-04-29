package com.qubacy.geoqq.data.geo.message.repository.source.http.module

import android.content.Context
import com.qubacy.geoqq.data.geo.message.repository.source.http.HttpGeoChatDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpGeoChatDataSourceModule {
    @Provides
    fun provideHttpGeoChatDataSource(
        @ApplicationContext context: Context
    ): HttpGeoChatDataSource {
        val httpApi = (context as CustomApplication).httpApi

        return httpApi.geoChatApi
    }
}