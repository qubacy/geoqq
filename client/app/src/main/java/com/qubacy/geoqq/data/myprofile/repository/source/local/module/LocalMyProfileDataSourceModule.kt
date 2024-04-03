package com.qubacy.geoqq.data.myprofile.repository.source.local.module

import android.content.Context
import com.qubacy.geoqq.data.myprofile.repository.source.local.LocalMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.local.myProfileDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class LocalMyProfileDataSourceModule {
    @Provides
    fun provideLocalMyProfileDataSource(
        @ApplicationContext context: Context
    ): LocalMyProfileDataSource {
        val myProfileDataStore = context.myProfileDataStore

        return LocalMyProfileDataSource(myProfileDataStore)
    }
}