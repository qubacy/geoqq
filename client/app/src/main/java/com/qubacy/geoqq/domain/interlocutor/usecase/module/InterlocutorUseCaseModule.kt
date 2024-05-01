package com.qubacy.geoqq.domain.interlocutor.usecase.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object InterlocutorUseCaseModule {
    @Provides
    fun provideInterlocutorUseCase(
        errorDataRepository: ErrorDataRepository,
        logoutUseCase: LogoutUseCase,
        userDataRepository: UserDataRepository
    ): InterlocutorUseCase {
        return InterlocutorUseCase(errorDataRepository, logoutUseCase, userDataRepository)
    }
}