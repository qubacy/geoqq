package com.qubacy.geoqq.data._common.repository._common.source.remote.http.api.module

import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.api.HttpApi
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpApiModule {
    @Provides
    fun provideHttpApi(
        @ApplicationContext context: Context
    ): HttpApi {
        val httpApi = (context as CustomApplication).httpApi

        return httpApi
    }
}