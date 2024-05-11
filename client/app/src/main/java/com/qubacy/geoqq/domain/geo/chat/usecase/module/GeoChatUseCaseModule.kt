package com.qubacy.geoqq.domain.geo.chat.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.geo.message.repository.impl.GeoMessageDataRepositoryImpl
import com.qubacy.geoqq.data.user.repository.impl.UserDataRepositoryImpl
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
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        mateRequestUseCase: MateRequestUseCase,
        interlocutorUseCase: InterlocutorUseCase,
        logoutUseCase: LogoutUseCase,
        geoMessageDataRepository: GeoMessageDataRepositoryImpl,
        userDataRepository: UserDataRepositoryImpl
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