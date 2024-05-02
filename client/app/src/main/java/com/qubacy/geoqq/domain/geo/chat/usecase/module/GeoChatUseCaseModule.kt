package com.qubacy.geoqq.domain.geo.chat.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.geo.message.repository.GeoMessageDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain.geo.chat.usecase.GeoChatUseCase
import com.qubacy.geoqq.domain.interlocutor.usecase.InterlocutorUseCase
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase.MateRequestUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GeoChatUseCaseModule {
    @Provides
    fun provideGeoChatUseCase(
        localErrorDataSource: LocalErrorDataSource,
        mateRequestUseCase: MateRequestUseCase,
        interlocutorUseCase: InterlocutorUseCase,
        logoutUseCase: LogoutUseCase,
        geoMessageDataRepository: GeoMessageDataRepository,
        userDataRepository: UserDataRepository
    ): GeoChatUseCase {
        return GeoChatUseCase(
            localErrorDataSource,
            mateRequestUseCase,
            interlocutorUseCase,
            logoutUseCase,
            geoMessageDataRepository,
            userDataRepository
        )
    }
}