package com.qubacy.geoqq.data.mate.message.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.http.HttpMateMessageDataSource
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
        localErrorDataSource: LocalErrorDataSource,
        userDataRepository: UserDataRepository,
        localMateMessageDataSource: LocalMateMessageDataSource,
        httpMateMessageDataSource: HttpMateMessageDataSource
    ): MateMessageDataRepository {
        return MateMessageDataRepository(
            mErrorSource = localErrorDataSource,
            mUserDataRepository = userDataRepository,
            mLocalMateMessageDataSource = localMateMessageDataSource,
            mHttpMateMessageDataSource = httpMateMessageDataSource
        )
    }
}