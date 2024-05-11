package com.qubacy.geoqq.domain.myprofile.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.myprofile.repository._common.MyProfileDataRepository
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.myprofile.usecase._common.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.usecase.impl.MyProfileUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MyProfileUseCaseModule {
    @Provides
    fun provideMyProfileUseCase(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        logoutUseCase: LogoutUseCase,
        myProfileDataRepository: MyProfileDataRepository,
        tokenDataRepository: AuthDataRepository
    ): MyProfileUseCase {
        return MyProfileUseCaseImpl(
            localErrorDataSource,
            logoutUseCase,
            myProfileDataRepository,
            tokenDataRepository
        )
    }
}