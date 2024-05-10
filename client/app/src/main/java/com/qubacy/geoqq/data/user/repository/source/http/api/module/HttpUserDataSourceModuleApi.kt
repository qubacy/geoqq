package com.qubacy.geoqq.data.user.repository.source.http.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.user.repository.source.http.api.HttpUserDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpUserDataSourceModuleApi {
    @Provides
    fun provideHttpUserDataSourceApi(
        httpRestApi: HttpRestApi
    ): HttpUserDataSourceApi {
        return httpRestApi.userApi
    }
}