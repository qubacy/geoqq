package com.qubacy.geoqq.data.mate.request.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.RemoteMateRequestHttpRestDataSource
import com.qubacy.geoqq.data.mate.request.repository.impl.MateRequestDataRepositoryImpl
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateRequestDataRepositoryModule {
    @Provides
    fun provideMateRequestDataRepository(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        userDataRepository: UserDataRepository,
        remoteMateRequestHttpRestDataSource: RemoteMateRequestHttpRestDataSource
    ): MateRequestDataRepository {
        return MateRequestDataRepositoryImpl(
            mErrorSource = localErrorDataSource,
            mUserDataRepository = userDataRepository,
            mRemoteMateRequestHttpRestDataSource = remoteMateRequestHttpRestDataSource
        )
    }
}