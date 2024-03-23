package com.qubacy.geoqq.data.user.repository.source.local.module

import android.content.Context
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalUserDataSourceModule {
    @Provides
    fun provideLocalUserDataSource(
        @ApplicationContext context: Context
    ): LocalUserDataSource {
        val db = (context as CustomApplication).db

        return db.userDao()
    }
}