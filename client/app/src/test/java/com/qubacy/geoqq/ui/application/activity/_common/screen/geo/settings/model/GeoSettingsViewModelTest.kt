package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.mock.LocationMockUtil
import com.qubacy.geoqq._common.point._test.util.PointUtils
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.location.model.operation.LocationPointChangedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.StatefulViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.operation.ChangeRadiusUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.state.GeoSettingsUiState
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class GeoSettingsViewModelTest(

) : StatefulViewModelTest<GeoSettingsUiState, GeoSettingsViewModel>() {
    override fun createViewModel(
        savedStateHandle: SavedStateHandle,
        errorDataSource: LocalErrorDataSource
    ): GeoSettingsViewModel {
        return GeoSettingsViewModel(savedStateHandle, errorDataSource)
    }

    @Test
    fun changeLastLocationTest() = runTest {
        val initLocationPoint = Point(0.0, 0.0)
        val initRadius = 0
        val initUiState = GeoSettingsUiState(
            lastLocationPoint = initLocationPoint, radius = initRadius)

        val location = LocationMockUtil.mockLocation(10.0, 10.0)

        val expectedLocationPoint = Point(location.latitude, location.longitude)

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mModel.changeLastLocation(location)

            val operation = awaitItem()

            Assert.assertEquals(LocationPointChangedUiOperation::class, operation::class)

            operation as LocationPointChangedUiOperation

            val finalUiState = mModel.uiState

            PointUtils.assertPoints(expectedLocationPoint, operation.locationPoint)
            PointUtils.assertPoints(expectedLocationPoint, finalUiState.lastLocationPoint!!)
        }
    }

    @Test
    fun setMapLoadingStatusTest() = runTest {
        val initLoadingState = false
        val initRadius = 0
        val initUiState = GeoSettingsUiState(isLoading = initLoadingState, radius = initRadius)

        val isLoaded = false

        val expectedLoadingState = true

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mModel.setMapLoadingStatus(isLoaded)

            val operation = awaitItem()

            Assert.assertEquals(SetLoadingStateUiOperation::class, operation::class)

            operation as SetLoadingStateUiOperation

            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedLoadingState, operation.isLoading)
            Assert.assertEquals(expectedLoadingState, finalUiState.isLoading)
        }
    }

    @Test
    fun getScaledRadiusTest() {
        class TestCase(
            val radius: Int,
            val scaleCoefficient: Float,
            val expectedScaledRadius: Int
        )

        val testCases = listOf(
            TestCase(
                GeoSettingsViewModel.DEFAULT_MIN_RADIUS,
                0.5f,
                GeoSettingsViewModel.DEFAULT_MIN_RADIUS
            ),
            TestCase(200, 1.2f, 240),
            TestCase(1000, 0.5f, 500),
            TestCase(
                GeoSettingsViewModel.DEFAULT_MAX_RADIUS,
                1.5f,
                GeoSettingsViewModel.DEFAULT_MAX_RADIUS
            )
        )

        for (testCase in testCases) {
            val gottenScaledRadius = mModel.getScaledRadius(
                testCase.radius, testCase.scaleCoefficient)

            Assert.assertEquals(testCase.expectedScaledRadius, gottenScaledRadius)
        }
    }

    @Test
    fun applyScaleForRadiusTest() = runTest {
        val initRadius = 1000
        val initUiState = GeoSettingsUiState(radius = initRadius)

        val scaleCoefficient = 0.5f

        val expectedRadius = (initRadius * scaleCoefficient).toInt()

        setUiState(initUiState)

        mModel.uiOperationFlow.test {
            mModel.applyScaleForRadius(scaleCoefficient)

            val operation = awaitItem()

            Assert.assertEquals(ChangeRadiusUiOperation::class, operation::class)

            operation as ChangeRadiusUiOperation

            val finalUiState = mModel.uiState

            Assert.assertEquals(expectedRadius, operation.radius)
            Assert.assertEquals(expectedRadius, finalUiState.radius)
        }
    }
}