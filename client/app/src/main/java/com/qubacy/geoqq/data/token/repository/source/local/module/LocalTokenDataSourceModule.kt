package com.qubacy.geoqq.data.token.repository.source.local.module

import android.content.Context
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.local.tokenDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalTokenDataSourceModule {
    @Provides
    fun provideLocalTokenDataSource(
        @ApplicationContext context: Context
    ): LocalTokenDataSource {
        val tokenDataStore = context.tokenDataStore

        return LocalTokenDataSource(tokenDataStore)
    }
}