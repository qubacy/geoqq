package com.qubacy.geoqq.data.user.repository._di.module

import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.data.user.repository.impl.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class UserDataRepositoryModule {
    @Binds
    abstract fun bindUserDataRepository(
        userDataRepository: UserDataRepositoryImpl
    ): UserDataRepository
}