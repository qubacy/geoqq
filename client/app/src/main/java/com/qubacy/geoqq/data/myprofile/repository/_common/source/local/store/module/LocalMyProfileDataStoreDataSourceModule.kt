package com.qubacy.geoqq.data.myprofile.repository._common.source.local.store.module

import android.content.Context
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.LocalMyProfileDataStoreDataSource
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.myProfileDataStore
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store.impl.LocalMyProfileDataStoreDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalMyProfileDataStoreDataSourceModule {
    @Provides
    fun provideLocalMyProfileDataStoreDataSource(
        @ApplicationContext context: Context
    ): LocalMyProfileDataStoreDataSource {
        val myProfileDataStore = context.myProfileDataStore

        return LocalMyProfileDataStoreDataSourceImpl(myProfileDataStore)
    }
}