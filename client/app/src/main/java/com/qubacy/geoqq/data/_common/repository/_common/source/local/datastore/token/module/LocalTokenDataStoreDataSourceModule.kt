package com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.module

import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data.auth.repository.source.local.store.tokenDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalTokenDataStoreDataSourceModule {
    @Provides
    fun provideLocalTokenDataStoreDataSource(
        @ApplicationContext context: Context
    ): LocalTokenDataStoreDataSource {
        val tokenDataStore = context.tokenDataStore

        return LocalTokenDataStoreDataSource(tokenDataStore)
    }
}