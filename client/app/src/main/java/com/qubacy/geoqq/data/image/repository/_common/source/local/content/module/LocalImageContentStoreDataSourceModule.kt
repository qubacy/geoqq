package com.qubacy.geoqq.data.image.repository._common.source.local.content.module

import android.content.Context
import com.qubacy.geoqq.data.image.repository._common.source.local.content._common.LocalImageContentStoreDataSource
import com.qubacy.geoqq.data.image.repository._common.source.local.content.impl.LocalImageContentStoreDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalImageContentStoreDataSourceModule {
    @Provides
    fun provideLocalImageContentStoreDataSource(
        @ApplicationContext context: Context
    ): LocalImageContentStoreDataSource {
        return LocalImageContentStoreDataSourceImpl(context.contentResolver)
    }
}