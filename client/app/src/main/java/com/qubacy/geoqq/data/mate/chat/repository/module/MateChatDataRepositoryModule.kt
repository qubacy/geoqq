package com.qubacy.geoqq.data.mate.chat.repository.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.http.HttpMateChatDataSource
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateChatDataRepositoryModule {
    @Provides
    fun provideMateChatDataRepository(
        localErrorDataSource: LocalErrorDataSource,
        userDataRepository: UserDataRepository,
        localMateChatDataSource: LocalMateChatDataSource,
        httpMateChatDataSource: HttpMateChatDataSource
    ): MateChatDataRepository {
        return MateChatDataRepository(
            mErrorSource = localErrorDataSource,
            mUserDataRepository = userDataRepository,
            mLocalMateChatDataSource = localMateChatDataSource,
            mHttpMateChatDataSource = httpMateChatDataSource
        )
    }
}