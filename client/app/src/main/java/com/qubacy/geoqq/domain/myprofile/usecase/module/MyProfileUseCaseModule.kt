package com.qubacy.geoqq.domain.myprofile.usecase.module

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.myprofile.repository.impl.MyProfileDataRepositoryImpl
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
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
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        logoutUseCase: LogoutUseCase,
        myProfileDataRepository: MyProfileDataRepositoryImpl,
        tokenDataRepository: AuthDataRepositoryImpl
    ): MyProfileUseCase {
        return MyProfileUseCase(
            localErrorDataSource,
            logoutUseCase,
            myProfileDataRepository,
            tokenDataRepository
        )
    }
}