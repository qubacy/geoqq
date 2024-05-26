package com.qubacy.geoqq.data.geo.message.repository._di.module

import com.qubacy.geoqq.data.geo.message.repository._common.GeoMessageDataRepository
import com.qubacy.geoqq.data.geo.message.repository.impl.GeoMessageDataRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class GeoMessageDataRepositoryModule {
    @Binds
    abstract fun bindGeoMessageDataRepository(
        geoMessageDataRepository: GeoMessageDataRepositoryImpl
    ): GeoMessageDataRepository
}