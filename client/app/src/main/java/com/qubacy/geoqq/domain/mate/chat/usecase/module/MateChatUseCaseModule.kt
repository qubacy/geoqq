package com.qubacy.geoqq.domain.mate.chat.usecase.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.message.repository.MateMessageDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain.mate.chat.usecase.MateChatUseCase
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
        mateMessageDataRepository: MateMessageDataRepository,
        userDataRepository: UserDataRepository
    ): MateChatUseCase {
        return MateChatUseCase(
            errorDataRepository, mateMessageDataRepository, userDataRepository
        )
    }
}