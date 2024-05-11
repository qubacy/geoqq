package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.impl.GeoSettingsViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.impl.GeoSettingsViewModelFactoryQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object GeoSettingsViewModelModule {
    @Provides
    @GeoSettingsViewModelFactoryQualifier
    fun provideGeoSettingsViewModelFactory(
        localErrorDataSource: LocalErrorDatabaseDataSource
    ): ViewModelProvider.Factory {
        return GeoSettingsViewModelFactory(localErrorDataSource)
    }
}