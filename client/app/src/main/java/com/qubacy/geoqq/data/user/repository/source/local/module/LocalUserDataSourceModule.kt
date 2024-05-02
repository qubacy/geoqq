package com.qubacy.geoqq.data.user.repository.source.local.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalUserDataSourceModule {
    @Provides
    fun provideLocalUserDataSource(
        database: Database
    ): LocalUserDataSource {
        return database.userDao()
    }
}