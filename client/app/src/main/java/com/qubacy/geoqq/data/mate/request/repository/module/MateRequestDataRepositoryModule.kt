package com.qubacy.geoqq.data.mate.request.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.source.http.HttpMateRequestDataSource
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateRequestDataRepositoryModule {
    @Provides
    fun provideMateRequestDataRepository(
        localErrorDataSource: LocalErrorDataSource,
        userDataRepository: UserDataRepository,
        httpMateRequestDataSource: HttpMateRequestDataSource
    ): MateRequestDataRepository {
        return MateRequestDataRepository(
            mErrorSource = localErrorDataSource,
            mUserDataRepository = userDataRepository,
            mHttpMateRequestDataSource = httpMateRequestDataSource
        )
    }
}