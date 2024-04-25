package com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.module

import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpCallExecutorModule {
    @Provides
    fun provideHttpCallExecutor(
        errorDataRepository: ErrorDataRepository,
        @ApplicationContext context: Context
    ): HttpCallExecutor {
        val httpApi = (context as CustomApplication).httpApi

        return HttpCallExecutor(errorDataRepository, httpApi.okHttpClient, httpApi.retrofit)
    }
}