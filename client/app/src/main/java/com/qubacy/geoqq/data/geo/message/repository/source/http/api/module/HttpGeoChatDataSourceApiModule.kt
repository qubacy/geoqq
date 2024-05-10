package com.qubacy.geoqq.data.geo.message.repository.source.http.api.module

import android.content.Context
import com.qubacy.geoqq.data.geo.message.repository.source.http.api.HttpGeoMessageDataSourceApi
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpGeoChatDataSourceApiModule {
    @Provides
    fun provideHttpGeoChatDataSourceApi(
        @ApplicationContext context: Context
    ): HttpGeoMessageDataSourceApi {
        val httpApi = (context as CustomApplication).httpRestApi

        return httpApi.geoChatApi
    }
}