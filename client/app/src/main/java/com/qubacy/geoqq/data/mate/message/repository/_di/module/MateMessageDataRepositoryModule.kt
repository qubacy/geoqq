package com.qubacy.geoqq.data.mate.message.repository._di.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.MateMessageDataRepository
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.LocalMateMessageDatabaseDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.RemoteMateMessageHttpRestDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.RemoteMateMessageHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.message.repository.impl.MateMessageDataRepositoryImpl
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import dagger.Module
import dagger.Provides

@Module
abstract class MateMessageDataRepositoryModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideMateMessageDataRepository(
            errorDataSource: LocalErrorDatabaseDataSource,
            userDataRepository: UserDataRepository,
            localMateMessageDatabaseDataSource: LocalMateMessageDatabaseDataSource,
            remoteMateMessageHttpRestDataSource: RemoteMateMessageHttpRestDataSource,
            remoteMateMessageHttpWebSocketDataSource: RemoteMateMessageHttpWebSocketDataSource
        ): MateMessageDataRepository {
            return MateMessageDataRepositoryImpl(
                mErrorSource = errorDataSource,
                mUserDataRepository = userDataRepository,
                mLocalMateMessageDatabaseDataSource = localMateMessageDatabaseDataSource,
                mRemoteMateMessageHttpRestDataSource = remoteMateMessageHttpRestDataSource,
                mRemoteMateMessageHttpWebSocketDataSource = remoteMateMessageHttpWebSocketDataSource
            )
        }
    }
}