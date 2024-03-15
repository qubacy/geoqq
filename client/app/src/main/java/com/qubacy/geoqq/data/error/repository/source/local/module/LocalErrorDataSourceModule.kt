package com.qubacy.geoqq.data.error.repository.source.local.module

import android.content.Context
import com.qubacy.geoqq.data.error.repository.source.local.LocalErrorDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalErrorDataSourceModule {
    @Provides
    fun provideLocalErrorDataSource(
        @ApplicationContext context: Context
    ): LocalErrorDataSource {
        return (context as CustomApplication).db.errorDao()
    }
}