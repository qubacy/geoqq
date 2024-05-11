package com.qubacy.geoqq.data.geo.message.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.GeoMessageDataRepository
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.RemoteGeoMessageHttpRestDataSource
import com.qubacy.geoqq.data.geo.message.repository.impl.GeoMessageDataRepositoryImpl
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GeoMessageDataRepositoryModule {
    @Provides
    fun provideGeoMessageDataRepository(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        userDataRepository: UserDataRepository,
        remoteGeoMessageHttpRestDataSource: RemoteGeoMessageHttpRestDataSource
    ): GeoMessageDataRepository {
        return GeoMessageDataRepositoryImpl(
            mErrorSource = localErrorDataSource,
            mUserDataRepository = userDataRepository,
            mRemoteGeoMessageHttpRestDataSource = remoteGeoMessageHttpRestDataSource,
        )
    }
}