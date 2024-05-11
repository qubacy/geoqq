package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.module.FakeViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.impl.GeoSettingsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.factory.FakeGeoSettingsViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.factory._test.mock.GeoSettingsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model._common.state.GeoSettingsUiState
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [ActivityRetainedComponent::class],
    replaces = [GeoSettingsViewModelModule::class]
)
object FakeGeoSettingsViewModelModule : FakeViewModelModule<
        GeoSettingsUiState, GeoSettingsViewModelMockContext
>() {
    @Provides
    @GeoSettingsViewModelFactoryQualifier
    fun provideFakeGeoSettingsViewModelFactory(): ViewModelProvider.Factory {
        return FakeGeoSettingsViewModelFactory(mockContext)
    }
}