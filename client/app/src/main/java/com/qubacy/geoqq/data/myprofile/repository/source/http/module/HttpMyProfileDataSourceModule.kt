package com.qubacy.geoqq.data.myprofile.repository.source.http.module

import android.content.Context
import com.qubacy.geoqq.data.myprofile.repository.source.http.HttpMyProfileDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpMyProfileDataSourceModule {
    @Provides
    fun provideHttpMyProfileDataSource(
        @ApplicationContext context: Context
    ): HttpMyProfileDataSource {
        val httpApi = (context as CustomApplication).httpApi

        return httpApi.myProfileApi
    }
}