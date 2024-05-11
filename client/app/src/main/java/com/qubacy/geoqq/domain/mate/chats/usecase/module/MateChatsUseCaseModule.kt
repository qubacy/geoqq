package com.qubacy.geoqq.domain.mate.chats.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.MateChatDataRepository
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase._common.MateChatsUseCase
import com.qubacy.geoqq.domain.mate.chats.usecase.impl.MateChatsUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateChatsUseCaseModule {
    @Provides
    fun provideMateChatsUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        logoutUseCase: LogoutUseCase,
        mateChatDataRepository: MateChatDataRepository
    ): MateChatsUseCase {
        return MateChatsUseCaseImpl(localErrorDataSource, logoutUseCase, mateChatDataRepository)
    }
}