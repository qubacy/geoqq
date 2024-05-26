package com.qubacy.geoqq.data.image.repository._di.module

import com.qubacy.geoqq.data.image.repository._common.ImageDataRepository
import com.qubacy.geoqq.data.image.repository.impl.ImageDataRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class ImageDataRepositoryModule {
    @Binds
    abstract fun bindImageDataRepository(
        imageDataRepository: ImageDataRepositoryImpl
    ): ImageDataRepository
}