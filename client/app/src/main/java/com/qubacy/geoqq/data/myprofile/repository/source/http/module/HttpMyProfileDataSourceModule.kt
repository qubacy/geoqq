package com.qubacy.geoqq.data.myprofile.repository.source.http.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.myprofile.repository.source.http.HttpMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.http.api.HttpMyProfileDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpMyProfileDataSourceModule {
    @Provides
    fun provideHttpMyProfileDataSource(
        httpMyProfileDataSourceApi: HttpMyProfileDataSourceApi,
        httpCallExecutor: HttpCallExecutor
    ): HttpMyProfileDataSource {
        return HttpMyProfileDataSource(httpMyProfileDataSourceApi, httpCallExecutor)
    }
}