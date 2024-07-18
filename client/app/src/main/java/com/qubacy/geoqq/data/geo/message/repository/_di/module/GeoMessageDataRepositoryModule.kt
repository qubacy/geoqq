package com.qubacy.geoqq.data.geo.message.repository._di.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.GeoMessageDataRepository
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.RemoteGeoMessageHttpRestDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.RemoteGeoMessageHttpWebSocketDataSource
import com.qubacy.geoqq.data.geo.message.repository.impl.GeoMessageDataRepositoryImpl
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import dagger.Module
import dagger.Provides

@Module
abstract class GeoMessageDataRepositoryModule {
    companion object {
        @JvmStatic
        @Provides
        fun provideGeoMessageDataRepository(
            errorDataSource: LocalErrorDatabaseDataSource,
            userDataRepository: UserDataRepository,
            remoteGeoMessageHttpRestDataSource: RemoteGeoMessageHttpRestDataSource,
            remoteGeoMessageHttpWebSocketDataSource: RemoteGeoMessageHttpWebSocketDataSource
        ): GeoMessageDataRepository {
            return GeoMessageDataRepositoryImpl(
                mErrorSource = errorDataSource,
                mUserDataRepository = userDataRepository,
                mRemoteGeoMessageHttpRestDataSource = remoteGeoMessageHttpRestDataSource,
                mRemoteGeoMessageHttpWebSocketDataSource = remoteGeoMessageHttpWebSocketDataSource
            )
        }
    }
}