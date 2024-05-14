package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.factory

import androidx.lifecycle.ViewModel
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.factory.FakeStatefulViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model._common.GeoSettingsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.factory._test.mock.GeoSettingsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model._common.state.GeoSettingsUiState
import org.mockito.Mockito

class FakeGeoSettingsViewModelFactory(
    mockContext: GeoSettingsViewModelMockContext
) : FakeStatefulViewModelFactory<
        GeoSettingsUiState, GeoSettingsViewModel, GeoSettingsViewModelMockContext
        >(mockContext) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModelMock = super.create(modelClass) as GeoSettingsViewModel

        Mockito.`when`(viewModelMock.changeLastLocation(AnyMockUtil.anyObject())).thenAnswer {
            mockContext.changeLastLocationCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.applyScaleForRadius(Mockito.anyFloat())).thenAnswer {
            mockContext.applyScaleForRadiusCallFlag = true

            Unit
        }
        Mockito.`when`(viewModelMock.setMapLoadingStatus(Mockito.anyBoolean())).thenAnswer {
            mockContext.setMapLoadingStatusCallFlag = true

            Unit
        }

        return viewModelMock as T
    }
}