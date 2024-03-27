package com.qubacy.geoqq.data.mate.chat.repository.source.http.module

import android.content.Context
import com.qubacy.geoqq.data.mate.chat.repository.source.http.HttpMateChatDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpMateChatDataSourceModule {
    @Provides
    fun provideHttpMateChatDataSource(
        @ApplicationContext context: Context
    ): HttpMateChatDataSource {
        val httpApi = (context as CustomApplication).httpApi

        return httpApi.mateChatApi
    }
}