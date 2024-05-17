package com.qubacy.geoqq.domain.mate.chat.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.MateChatDataRepository
import com.qubacy.geoqq.data.mate.message.repository._common.MateMessageDataRepository
import com.qubacy.geoqq.domain.user.usecase._common.UserUseCase
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.mate.chat.usecase._common.MateChatUseCase
import com.qubacy.geoqq.domain.mate.chat.usecase.impl.MateChatUseCaseImpl
import com.qubacy.geoqq.domain.mate.request.usecase._common.MateRequestUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateChatUseCaseModule {
    @Provides
    fun provideMateChatUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        mateRequestUseCase: MateRequestUseCase,
        interlocutorUseCase: UserUseCase,
        mLogoutUseCase: LogoutUseCase,
        mateMessageDataRepository: MateMessageDataRepository,
        mateChatDataRepository: MateChatDataRepository
    ): MateChatUseCase {
        return MateChatUseCaseImpl(
            localErrorDataSource,
            mateRequestUseCase,
            interlocutorUseCase,
            mLogoutUseCase,
            mateMessageDataRepository,
            mateChatDataRepository
        )
    }
}