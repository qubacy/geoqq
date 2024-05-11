package com.qubacy.geoqq.data.image.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.image.repository._common.ImageDataRepository
import com.qubacy.geoqq.data.image.repository._common.source.local.content._common.LocalImageContentStoreDataSource
import com.qubacy.geoqq.data.image.repository.impl.ImageDataRepositoryImpl
import com.qubacy.geoqq.data.image.repository._common.source.remote.http.rest._common.RemoteImageHttpRestDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ImageDataRepositoryModule {
    @Provides
    fun provideImageDataRepository(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        localImageContentStoreDataSource: LocalImageContentStoreDataSource,
        remoteImageHttpRestDataSource: RemoteImageHttpRestDataSource
    ): ImageDataRepository {
        return ImageDataRepositoryImpl(
            localErrorDataSource,
            localImageContentStoreDataSource,
            remoteImageHttpRestDataSource
        )
    }
}