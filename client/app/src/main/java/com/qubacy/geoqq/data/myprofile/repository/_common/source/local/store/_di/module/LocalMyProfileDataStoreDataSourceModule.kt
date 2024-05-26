package com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._di.module

import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.LocalMyProfileDataStoreDataSource
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store.impl.LocalMyProfileDataStoreDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class LocalMyProfileDataStoreDataSourceModule {
    @Binds
    abstract fun bindLocalMyProfileDataStoreDataSource(
        localMyProfileDataStoreDataSource: LocalMyProfileDataStoreDataSourceImpl
    ): LocalMyProfileDataStoreDataSource
}