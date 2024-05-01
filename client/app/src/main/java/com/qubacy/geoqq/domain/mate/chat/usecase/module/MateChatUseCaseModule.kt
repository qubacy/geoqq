package com.qubacy.geoqq.domain.mate.chat.usecase.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.chat.repository.MateChatDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
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
        errorDataRepository: ErrorDataRepository,
        mateRequestUseCase: MateRequestUseCase,
        interlocutorUseCase: InterlocutorUseCase,
        mLogoutUseCase: LogoutUseCase,
        mateMessageDataRepository: MateMessageDataRepository,
        mateChatDataRepository: MateChatDataRepository
    ): MateChatUseCase {
        return MateChatUseCase(
            errorDataRepository,
            mateRequestUseCase,
            interlocutorUseCase,
            mLogoutUseCase,
            mateMessageDataRepository,
            mateChatDataRepository
        )
    }
}