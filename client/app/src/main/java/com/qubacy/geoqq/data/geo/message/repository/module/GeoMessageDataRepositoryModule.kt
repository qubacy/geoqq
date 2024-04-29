package com.qubacy.geoqq.data.geo.message.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.geo.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.geo.message.repository.source.http.HttpGeoChatDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
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
        errorDataRepository: ErrorDataRepository,
        tokenDataRepository: TokenDataRepository,
        userDataRepository: UserDataRepository,
        httpGeoChatDataSource: HttpGeoChatDataSource,
        httpCallExecutor: HttpCallExecutor
    ): GeoMessageDataRepository {
        return GeoMessageDataRepository(
            mErrorDataRepository = errorDataRepository,
            mTokenDataRepository = tokenDataRepository,
            mUserDataRepository = userDataRepository,
            mHttpGeoChatDataSource = httpGeoChatDataSource,
            mHttpCallExecutor = httpCallExecutor
        )
    }
}