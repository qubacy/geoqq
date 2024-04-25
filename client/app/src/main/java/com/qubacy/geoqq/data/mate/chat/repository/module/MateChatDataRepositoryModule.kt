package com.qubacy.geoqq.data.mate.chat.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.http._common.executor.HttpCallExecutor
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.source.http.HttpMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateChatDataRepositoryModule {
    @Provides
    fun provideMateChatDataRepository(
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        userDataRepository: UserDataRepository,
        localMateChatDataSource: LocalMateChatDataSource,
        httpMateChatDataSource: HttpMateChatDataSource,
        httpCallExecutor: HttpCallExecutor
    ): MateChatDataRepository {
        return MateChatDataRepository(
            mErrorDataRepository = errorDataRepository,
            mTokenDataRepository = tokenDataRepository,
            mUserDataRepository = userDataRepository,
            mLocalMateChatDataSource = localMateChatDataSource,
            mHttpMateChatDataSource = httpMateChatDataSource,
            mHttpCallExecutor = httpCallExecutor
        )
    }
}