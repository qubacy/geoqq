package com.qubacy.geoqq.domain.myprofile.usecase.module

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
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
        errorDataRepository: ErrorDataRepository,
        myProfileDataRepository: MyProfileDataRepository,
        tokenDataRepository: TokenDataRepository
    ): MyProfileUseCase {
        return MyProfileUseCase(
            errorDataRepository,
            myProfileDataRepository,
            tokenDataRepository
        )
    }
}