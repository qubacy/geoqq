package com.qubacy.geoqq.data.myprofile.repository.source.http.api.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.rest.api.HttpRestApi
import com.qubacy.geoqq.data.myprofile.repository.source.http.api.HttpMyProfileDataSourceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object HttpMyProfileDataSourceModuleApi {
    @Provides
    fun provideHttpMyProfileDataSourceApi(
        httpRestApi: HttpRestApi
    ): HttpMyProfileDataSourceApi {
        return httpRestApi.myProfileApi
    }
}