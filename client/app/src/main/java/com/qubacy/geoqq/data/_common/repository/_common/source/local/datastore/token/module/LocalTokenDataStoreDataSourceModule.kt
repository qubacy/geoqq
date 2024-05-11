package com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.module

import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
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
        val application = (context as CustomApplication)

        return application.localTokenDataStoreDataSource
    }
}