package com.qubacy.geoqq.data.token.repository.source.local.store.module

import android.content.Context
import com.qubacy.geoqq.data.token.repository.source.local.store.LocalStoreTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.local.store.tokenDataStore
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
    ): LocalStoreTokenDataSource {
        val tokenDataStore = context.tokenDataStore

        return LocalStoreTokenDataSource(tokenDataStore)
    }
}