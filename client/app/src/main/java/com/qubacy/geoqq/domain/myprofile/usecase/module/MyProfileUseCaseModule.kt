package com.qubacy.geoqq.domain.myprofile.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.auth.repository.AuthDataRepository
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.myprofile.usecase.MyProfileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object MyProfileUseCaseModule {
    @Provides
    fun provideMyProfileUseCase(
        localErrorDataSource: LocalErrorDataSource,
        logoutUseCase: LogoutUseCase,
        myProfileDataRepository: MyProfileDataRepository,
        tokenDataRepository: AuthDataRepository
    ): MyProfileUseCase {
        return MyProfileUseCase(
            localErrorDataSource,
            logoutUseCase,
            myProfileDataRepository,
            tokenDataRepository
        )
    }
}