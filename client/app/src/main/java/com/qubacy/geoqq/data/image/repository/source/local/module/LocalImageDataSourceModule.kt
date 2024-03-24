package com.qubacy.geoqq.data.image.repository.source.local.module

import android.content.Context
import com.qubacy.geoqq.data.image.repository.source.local.LocalImageDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalImageDataSourceModule {
    @Provides
    fun provideLocalImageDataSource(
        @ApplicationContext context: Context
    ): LocalImageDataSource {
        return LocalImageDataSource(context.contentResolver)
    }
}