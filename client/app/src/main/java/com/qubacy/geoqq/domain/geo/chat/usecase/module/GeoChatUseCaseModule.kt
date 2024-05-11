package com.qubacy.geoqq.domain.geo.chat.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.GeoMessageDataRepository
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.domain.geo.chat.usecase.GeoChatUseCaseImpl
import com.qubacy.geoqq.domain.geo.chat.usecase._common.GeoChatUseCase
import com.qubacy.geoqq.domain.interlocutor.usecase._common.InterlocutorUseCase
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.mate.request.usecase._common.MateRequestUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object GeoChatUseCaseModule {
    @Provides
    fun provideGeoChatUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        mateRequestUseCase: MateRequestUseCase,
        interlocutorUseCase: InterlocutorUseCase,
        logoutUseCase: LogoutUseCase,
        geoMessageDataRepository: GeoMessageDataRepository,
        userDataRepository: UserDataRepository
    ): GeoChatUseCase {
        return GeoChatUseCaseImpl(
            localErrorDataSource,
            mateRequestUseCase,
            interlocutorUseCase,
            logoutUseCase,
            geoMessageDataRepository,
            userDataRepository
        )
    }
}