package com.qubacy.geoqq.domain.interlocutor.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.user.repository.impl.UserDataRepositoryImpl
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
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        logoutUseCase: LogoutUseCase,
        userDataRepository: UserDataRepositoryImpl
    ): InterlocutorUseCase {
        return InterlocutorUseCase(localErrorDataSource, logoutUseCase, userDataRepository)
    }
}