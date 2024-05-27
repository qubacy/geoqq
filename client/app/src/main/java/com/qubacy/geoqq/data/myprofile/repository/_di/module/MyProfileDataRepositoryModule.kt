package com.qubacy.geoqq.data.myprofile.repository._di.module

import com.qubacy.geoqq.data.myprofile.repository._common.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.impl.MyProfileDataRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class MyProfileDataRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindMyProfileDataRepository(
        myProfileDataRepository: MyProfileDataRepositoryImpl
    ): MyProfileDataRepository
}