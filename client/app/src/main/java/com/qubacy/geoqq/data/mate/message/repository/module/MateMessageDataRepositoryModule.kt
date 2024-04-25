package com.qubacy.geoqq.data.mate.message.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.source.http.HttpMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateMessageDataRepositoryModule {
    @Provides
    fun provideMateMessageDataRepository(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        userDataRepository: UserDataRepository,
        localMateMessageDataSource: LocalMateMessageDataSource,
        httpMateMessageDataSource: HttpMateMessageDataSource,
        httpCallExecutor: HttpCallExecutor
    ): MateMessageDataRepository {
        return MateMessageDataRepository(
            mErrorDataRepository = errorDataRepository,
            mTokenDataRepository = tokenDataRepository,
            mUserDataRepository = userDataRepository,
            mLocalMateMessageDataSource = localMateMessageDataSource,
            mHttpMateMessageDataSource = httpMateMessageDataSource,
            mHttpCallExecutor = httpCallExecutor
        )
    }
}