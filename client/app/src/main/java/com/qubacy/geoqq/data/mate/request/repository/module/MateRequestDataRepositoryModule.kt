package com.qubacy.geoqq.data.mate.request.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository.source.http.HttpMateRequestDataSource
import com.qubacy.geoqq.data.auth.repository.AuthDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class MateRequestDataRepositoryModule {
    @Provides
    fun provideMateRequestDataRepository(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: AuthDataRepository,
        userDataRepository: UserDataRepository,
        httpMateRequestDataSource: HttpMateRequestDataSource,
        httpCallExecutor: HttpCallExecutor
    ): MateRequestDataRepository {
        return MateRequestDataRepository(
            mErrorDataRepository = errorDataRepository,
            mTokenDataRepository = tokenDataRepository,
            mUserDataRepository = userDataRepository,
            mHttpMateRequestDataSource = httpMateRequestDataSource,
            mHttpCallExecutor = httpCallExecutor
        )
    }
}