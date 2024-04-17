package com.qubacy.geoqq.domain.mate.requests.usecase.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain.mate.requests.usecase.MateRequestsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MateRequestsUseCaseModule {
    @Provides
    fun provideMateRequestsUseCase(
        errorDataRepository: ErrorDataRepository,
        mateRequestDataRepository: MateRequestDataRepository,
        userDataRepository: UserDataRepository
    ): MateRequestsUseCase {
        return MateRequestsUseCase(
            errorDataRepository, mateRequestDataRepository, userDataRepository)
    }
}