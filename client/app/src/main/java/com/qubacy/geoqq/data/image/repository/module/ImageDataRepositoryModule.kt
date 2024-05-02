package com.qubacy.geoqq.data.image.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import com.qubacy.geoqq.data.image.repository.source.http.HttpImageDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ImageDataRepositoryModule {
    @Provides
    fun provideImageDataRepository(
        localErrorDataSource: LocalErrorDataSource,
        localImageDataSource: LocalImageDataSource,
        httpImageDataSource: HttpImageDataSource
    ): ImageDataRepository {
        return ImageDataRepository(
            localErrorDataSource,
            localImageDataSource,
            httpImageDataSource
        )
    }
}