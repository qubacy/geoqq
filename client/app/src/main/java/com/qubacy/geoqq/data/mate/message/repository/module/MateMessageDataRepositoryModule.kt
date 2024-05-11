package com.qubacy.geoqq.data.mate.message.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.LocalMateMessageDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.RemoteMateMessageHttpRestDataSource
import com.qubacy.geoqq.data.mate.message.repository.impl.MateMessageDataRepositoryImpl
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateMessageDataRepositoryModule {
    @Provides
    fun provideMateMessageDataRepository(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        userDataRepository: UserDataRepository,
        localMateMessageDatabaseDataSource: LocalMateMessageDatabaseDataSource,
        remoteMateMessageHttpRestDataSource: RemoteMateMessageHttpRestDataSource
    ): MateMessageDataRepository {
        return MateMessageDataRepositoryImpl(
            mErrorSource = localErrorDataSource,
            mUserDataRepository = userDataRepository,
            mLocalMateMessageDatabaseDataSource = localMateMessageDatabaseDataSource,
            mRemoteMateMessageHttpRestDataSource = remoteMateMessageHttpRestDataSource
        )
    }
}