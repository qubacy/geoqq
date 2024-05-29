package com.qubacy.geoqq.data.user.repository._di.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.local.datastore._common.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data.image.repository._common.ImageDataRepository
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.LocalUserDatabaseDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.RemoteUserHttpRestDataSource
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common.RemoteUserHttpWebSocketDataSource
import com.qubacy.geoqq.data.user.repository.impl.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class UserDataRepositoryModule {
    companion object {
        @JvmStatic
        @Singleton
        @Provides
        fun provideUserDataRepository(
            errorDataSource: LocalErrorDatabaseDataSource,
            imageDataRepository: ImageDataRepository,
            localTokenDataStoreDataSource: LocalTokenDataStoreDataSource,
            localUserDatabaseDataSource: LocalUserDatabaseDataSource,
            remoteUserHttpRestDataSource: RemoteUserHttpRestDataSource,
            remoteUserHttpWebSocketDataSource: RemoteUserHttpWebSocketDataSource
        ): UserDataRepository {
            return UserDataRepositoryImpl(
                mErrorSource = errorDataSource,
                mImageDataRepository = imageDataRepository,
                mLocalTokenDataStoreDataSource = localTokenDataStoreDataSource,
                mLocalUserDatabaseDataSource = localUserDatabaseDataSource,
                mRemoteUserHttpRestDataSource = remoteUserHttpRestDataSource,
                mRemoteUserHttpWebSocketDataSource = remoteUserHttpWebSocketDataSource
            )
        }
    }
}