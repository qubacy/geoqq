package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model._di.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.impl.GeoSettingsViewModelImplFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.impl.GeoSettingsViewModelFactoryQualifier
import dagger.Binds
import dagger.Module

@Module
abstract class GeoSettingsViewModelModule {
    @Binds
    @GeoSettingsViewModelFactoryQualifier
    abstract fun bindGeoSettingsViewModelFactory(
        geoSettingsViewModelFactory: GeoSettingsViewModelImplFactory
    ): ViewModelProvider.Factory
}