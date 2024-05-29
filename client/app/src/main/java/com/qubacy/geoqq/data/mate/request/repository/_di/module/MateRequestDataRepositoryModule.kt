package com.qubacy.geoqq.data.mate.request.repository._di.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.MateRequestDataRepository
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.RemoteMateRequestHttpRestDataSource
import com.qubacy.geoqq.data.mate.request.repository.impl.MateRequestDataRepositoryImpl
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class MateRequestDataRepositoryModule {
    companion object {
        @JvmStatic
        @Singleton
        @Provides
        fun provideMateRequestDataRepository(
            errorDataSource: LocalErrorDatabaseDataSource,
            userDataRepository: UserDataRepository,
            remoteMateRequestHttpRestDataSource: RemoteMateRequestHttpRestDataSource
        ): MateRequestDataRepository {
            return MateRequestDataRepositoryImpl(
                mErrorSource = errorDataSource,
                mUserDataRepository = userDataRepository,
                mRemoteMateRequestHttpRestDataSource = remoteMateRequestHttpRestDataSource
            )
        }
    }
}