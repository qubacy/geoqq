package com.qubacy.geoqq.data.user.repository.source.http.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.api.HttpApi
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
        httpApi: HttpApi
    ): HttpUserDataSourceApi {
        return httpApi.userApi
    }
}