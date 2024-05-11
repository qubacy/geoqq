package com.qubacy.geoqq.domain.mate.chat.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.mate.chat.repository.impl.MateChatDataRepositoryImpl
import com.qubacy.geoqq.data.mate.message.repository.impl.MateMessageDataRepositoryImpl
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.mate.chat.usecase.MateChatUseCase
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateChatUseCaseModule {
    @Provides
    fun provideMateChatUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        mateRequestUseCase: MateRequestUseCase,
        interlocutorUseCase: InterlocutorUseCase,
        mLogoutUseCase: LogoutUseCase,
        mateMessageDataRepository: MateMessageDataRepositoryImpl,
        mateChatDataRepository: MateChatDataRepositoryImpl
    ): MateChatUseCase {
        return MateChatUseCase(
            localErrorDataSource,
            mateRequestUseCase,
            interlocutorUseCase,
            mLogoutUseCase,
            mateMessageDataRepository,
            mateChatDataRepository
        )
    }
}