package com.qubacy.geoqq.domain.mate.chats.usecase.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
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
        errorDataRepository: ErrorDataRepository,

    ): MateChatsUseCase {
        return MateChatsUseCase(errorDataRepository, )
    }
}