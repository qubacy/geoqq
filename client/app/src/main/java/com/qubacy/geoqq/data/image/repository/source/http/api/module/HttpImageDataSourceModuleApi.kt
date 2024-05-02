package com.qubacy.geoqq.data.image.repository.source.http.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.api.HttpApi
import com.qubacy.geoqq.data.image.repository.source.http.api.HttpImageDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpImageDataSourceModuleApi {
    @Provides
    fun provideHttpImageDataSourceApi(
        httpApi: HttpApi
    ): HttpImageDataSourceApi {
        return httpApi.imageApi
    }
}