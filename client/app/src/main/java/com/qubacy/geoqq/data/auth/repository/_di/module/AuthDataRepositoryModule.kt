package com.qubacy.geoqq.data.auth.repository._di.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._di.module.WebSocketAdapterCreateQualifier
import com.qubacy.geoqq.data._common.repository.token.repository._common.TokenDataRepository
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.auth.repository._common.source.local.database._common.LocalAuthDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.RemoteAuthHttpRestDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.websocket._common.RemoteAuthHttpWebSocketDataSource
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AuthDataRepositoryModule {
    companion object {
        @JvmStatic
        @Singleton
        @Provides
        fun provideAuthDataRepository(
            errorDataSource: LocalErrorDatabaseDataSource,
            localAuthDatabaseDataSource: LocalAuthDatabaseDataSource,
            remoteAuthHttpRestDataSource: RemoteAuthHttpRestDataSource,
            remoteAuthHttpWebSocketDataSource: RemoteAuthHttpWebSocketDataSource,
            tokenDataRepository: TokenDataRepository,
            @WebSocketAdapterCreateQualifier webSocketAdapter: WebSocketAdapter
        ): AuthDataRepository {
            return AuthDataRepositoryImpl(
                mErrorSource = errorDataSource,
                mLocalAuthDatabaseDataSource = localAuthDatabaseDataSource,
                mRemoteAuthHttpRestDataSource = remoteAuthHttpRestDataSource,
                mRemoteAuthHttpWebSocketDataSource = remoteAuthHttpWebSocketDataSource,
                mTokenDataRepository = tokenDataRepository,
                mWebSocketAdapter = webSocketAdapter
            )
        }
    }
}