package com.qubacy.geoqq.data.image.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.source.http.HttpImageDataSource
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ImageDataRepositoryModule {
    @Provides
    fun provideImageDataRepository(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        localImageDataSource: LocalImageDataSource,
        httpImageDataSource: HttpImageDataSource,
        httpCallExecutor: HttpCallExecutor
    ): ImageDataRepository {
        return ImageDataRepository(
            errorDataRepository,
            tokenDataRepository,
            localImageDataSource,
            httpImageDataSource,
            httpCallExecutor
        )
    }
}