package com.qubacy.geoqq.domain.interlocutor.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.domain.interlocutor.usecase.impl.InterlocutorUseCaseImpl
import com.qubacy.geoqq.domain.interlocutor.usecase._common.InterlocutorUseCase
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object InterlocutorUseCaseModule {
    @Provides
    fun provideInterlocutorUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        logoutUseCase: LogoutUseCase,
        userDataRepository: UserDataRepository
    ): InterlocutorUseCase {
        return InterlocutorUseCaseImpl(localErrorDataSource, logoutUseCase, userDataRepository)
    }
}