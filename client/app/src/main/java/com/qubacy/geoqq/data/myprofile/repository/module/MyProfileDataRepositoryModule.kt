package com.qubacy.geoqq.data.myprofile.repository.module

import com.qubacy.geoqq.data.myprofile.repository._common.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.impl.MyProfileDataRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class MyProfileDataRepositoryModule {
    @Binds
    abstract fun bindMyProfileDataRepository(
        myProfileDataRepository: MyProfileDataRepositoryImpl
    ): MyProfileDataRepository
}