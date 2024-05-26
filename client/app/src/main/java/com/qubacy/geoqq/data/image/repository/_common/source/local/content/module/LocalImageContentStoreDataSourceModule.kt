package com.qubacy.geoqq.data.image.repository._common.source.local.content.module

import com.qubacy.geoqq.data.image.repository._common.source.local.content._common.LocalImageContentStoreDataSource
import com.qubacy.geoqq.data.image.repository._common.source.local.content.impl.LocalImageContentStoreDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class LocalImageContentStoreDataSourceModule {
    @Binds
    abstract fun bindLocalImageContentStoreDataSource(
        localImageContentStoreDataSource: LocalImageContentStoreDataSourceImpl
    ): LocalImageContentStoreDataSource
}