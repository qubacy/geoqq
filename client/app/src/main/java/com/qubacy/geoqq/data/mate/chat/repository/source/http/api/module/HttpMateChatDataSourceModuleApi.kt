package com.qubacy.geoqq.data.mate.chat.repository.source.http.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.api.HttpApi
import com.qubacy.geoqq.data.mate.chat.repository.source.http.api.HttpMateChatDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpMateChatDataSourceModuleApi {
    @Provides
    fun provideHttpMateChatDataSourceApi(
        httpApi: HttpApi
    ): HttpMateChatDataSourceApi {
        return httpApi.mateChatApi
    }
}