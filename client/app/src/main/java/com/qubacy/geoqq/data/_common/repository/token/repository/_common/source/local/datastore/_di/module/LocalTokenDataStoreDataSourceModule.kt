package com.qubacy.geoqq.data._common.repository.token.repository._common.source.local.datastore._di.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.local.datastore._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.local.datastore._common.tokenDataStore
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.local.datastore.impl.LocalTokenDataStoreDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class LocalTokenDataStoreDataSourceModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideLocalTokenDataStore(
            context: Context
        ): DataStore<Preferences> {
            return context.tokenDataStore
        }
    }

    @Singleton
    @Binds
    abstract fun bindLocalTokenDataStoreDataSource(
        localTokenDataStoreDataSource: LocalTokenDataStoreDataSourceImpl
    ): LocalTokenDataStoreDataSource
}