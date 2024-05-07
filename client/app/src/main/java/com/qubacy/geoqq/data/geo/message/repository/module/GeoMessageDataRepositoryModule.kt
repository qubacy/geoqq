package com.qubacy.geoqq.data.geo.message.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.geo.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.geo.message.repository.source.http.HttpGeoMessageDataSource
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GeoMessageDataRepositoryModule {
    @Provides
    fun provideGeoMessageDataRepository(
        localErrorDataSource: LocalErrorDataSource,
        userDataRepository: UserDataRepository,
        httpGeoMessageDataSource: HttpGeoMessageDataSource
    ): GeoMessageDataRepository {
        return GeoMessageDataRepository(
            mErrorSource = localErrorDataSource,
            mUserDataRepository = userDataRepository,
            mHttpGeoMessageDataSource = httpGeoMessageDataSource,
        )
    }
}