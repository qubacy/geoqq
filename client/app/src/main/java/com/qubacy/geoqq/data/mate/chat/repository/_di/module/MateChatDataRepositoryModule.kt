package com.qubacy.geoqq.data.mate.chat.repository._di.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.LocalMateChatDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.remote.http.rest._common.RemoteMateChatHttpRestDataSource
import com.qubacy.geoqq.data.mate.chat.repository.impl.MateChatDataRepositoryImpl
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class MateChatDataRepositoryModule {
    companion object {
        @JvmStatic
        @Singleton
        @Provides
        fun provideMateChatDataRepository(
            errorDataSource: LocalErrorDatabaseDataSource,
            userDataRepository: UserDataRepository,
            localMateChatDatabaseDataSource: LocalMateChatDatabaseDataSource,
            remoteMateChatHttpRestDataSource: RemoteMateChatHttpRestDataSource
        ): MateChatDataRepository {
            return MateChatDataRepositoryImpl(
                mErrorSource = errorDataSource,
                mUserDataRepository = userDataRepository,
                mLocalMateChatDatabaseDataSource = localMateChatDatabaseDataSource,
                mRemoteMateChatHttpRestDataSource = remoteMateChatHttpRestDataSource
            )
        }
    }
}