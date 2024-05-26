package com.qubacy.geoqq.domain.myprofile.usecase._di.module

import com.qubacy.geoqq.domain.myprofile.usecase._common.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.usecase.impl.MyProfileUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
abstract class MyProfileUseCaseModule {
    @Binds
    abstract fun bindMyProfileUseCase(myProfileUseCase: MyProfileUseCaseImpl): MyProfileUseCase
}