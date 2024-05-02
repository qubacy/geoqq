package com.qubacy.geoqq.domain.mate.chats.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase.MateChatsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateChatsUseCaseModule {
    @Provides
    fun provideMateChatsUseCase(
        localErrorDataSource: LocalErrorDataSource,
        logoutUseCase: LogoutUseCase,
        mateChatDataRepository: MateChatDataRepository
    ): MateChatsUseCase {
        return MateChatsUseCase(localErrorDataSource, logoutUseCase, mateChatDataRepository)
    }
}