package com.qubacy.geoqq.data.mate.message.repository.source.http.module

import android.content.Context
import com.qubacy.geoqq.data.mate.message.repository.source.http.HttpMateMessageDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class HttpMateMessageDataSourceModule {
    @Provides
    fun provideHttpMateMessageDataSourceModule(
        @ApplicationContext context: Context
    ): HttpMateMessageDataSource {
        val application = (context as CustomApplication)

        return application.httpApi.mateMessageApi
    }
}