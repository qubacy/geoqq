package com.qubacy.geoqq.domain.user.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.domain.user.usecase.impl.UserUseCaseImpl
import com.qubacy.geoqq.domain.user.usecase._common.UserUseCase
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UserUseCaseModule {
    @Provides
    fun provideUserUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        logoutUseCase: LogoutUseCase,
        userDataRepository: UserDataRepository
    ): UserUseCase {
        return UserUseCaseImpl(localErrorDataSource, logoutUseCase, userDataRepository)
    }
}