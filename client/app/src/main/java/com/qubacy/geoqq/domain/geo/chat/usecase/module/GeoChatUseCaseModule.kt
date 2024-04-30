package com.qubacy.geoqq.domain.geo.chat.usecase.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.geo.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain.geo.chat.usecase.GeoChatUseCase
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class GeoChatUseCaseModule {
    @Provides
    fun provideGeoChatUseCase(
        errorDataRepository: ErrorDataRepository,
        mateRequestUseCase: MateRequestUseCase,
        interlocutorUseCase: InterlocutorUseCase,
        geoMessageDataRepository: GeoMessageDataRepository,
        userDataRepository: UserDataRepository
    ): GeoChatUseCase {
        return GeoChatUseCase(
            errorDataRepository,
            mateRequestUseCase,
            interlocutorUseCase,
            geoMessageDataRepository,
            userDataRepository
        )
    }
}