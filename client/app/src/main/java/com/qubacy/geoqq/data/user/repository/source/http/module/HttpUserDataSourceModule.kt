package com.qubacy.geoqq.data.user.repository.source.http.module

import android.content.Context
import com.qubacy.geoqq.data.user.repository.source.http.HttpUserDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpUserDataSourceModule {
    @Provides
    fun provideHttpUserDataSource(
        @ApplicationContext context: Context
    ): HttpUserDataSource {
        val httpApi = (context as CustomApplication).httpApi

        return httpApi.userApi
    }
}