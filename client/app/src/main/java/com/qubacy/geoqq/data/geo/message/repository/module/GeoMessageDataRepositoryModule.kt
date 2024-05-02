package com.qubacy.geoqq.data.geo.message.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.geo.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.geo.message.repository.source.http.HttpGeoChatDataSource
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
        httpGeoChatDataSource: HttpGeoChatDataSource
    ): GeoMessageDataRepository {
        return GeoMessageDataRepository(
            mErrorSource = localErrorDataSource,
            mUserDataRepository = userDataRepository,
            mHttpGeoChatDataSource = httpGeoChatDataSource,
        )
    }
}