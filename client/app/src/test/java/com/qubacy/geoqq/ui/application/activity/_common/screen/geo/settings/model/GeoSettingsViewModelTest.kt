package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model

import android.location.Location
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.mock.LocationMockUtil
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.model.operation.LocationPointChangedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.StatefulViewModelTest
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.operation.ChangeRadiusUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.state.GeoSettingsUiState
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class GeoSettingsViewModelTest(

) : StatefulViewModelTest<GeoSettingsUiState, GeoSettingsViewModel>() {
    override fun createViewModel(
        savedStateHandle: SavedStateHandle,
        errorDataRepository: ErrorDataRepository
    ): GeoSettingsViewModel {
        return GeoSettingsViewModel(savedStateHandle, errorDataRepository)
    }

    @Test
    fun changeLastLocationTest() = runTest {
        val initLocationPoint = Point(0.0, 0.0)
        val initRadius = 0f
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

            assertLocationPoints(expectedLocationPoint, operation.locationPoint)
            assertLocationPoints(expectedLocationPoint, finalUiState.lastLocationPoint!!)
        }
    }

    @Test
    fun setMapLoadingStatusTest() = runTest {
        val initLoadingState = false
        val initRadius = 0f
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
            val radius: Float,
            val scaleCoefficient: Float,
            val expectedScaledRadius: Float
        )

        val testCases = listOf(
            TestCase(
                GeoSettingsViewModel.DEFAULT_MIN_RADIUS,
                0.5f,
                GeoSettingsViewModel.DEFAULT_MIN_RADIUS
            ),
            TestCase(200f, 1.2f, 240f),
            TestCase(1000f, 0.5f, 500f),
            TestCase(
                GeoSettingsViewModel.DEFAULT_MAX_RADIUS,
                1.5f,
                GeoSettingsViewModel.DEFAULT_MAX_RADIUS
            )
        )

        for (testCase in testCases) {
            val gottenScaledRadius = mModel.getScaledRadius(
                testCase.radius, testCase.scaleCoefficient)

            Assert.assertEquals(testCase.expectedScaledRadius, gottenScaledRadius, 0.01f)
        }
    }

    @Test
    fun applyScaleForRadiusTest() = runTest {
        val initRadius = 1000f
        val initUiState = GeoSettingsUiState(radius = initRadius)

        val scaleCoefficient = 0.5f

        val expectedRadius = initRadius * scaleCoefficient

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

    private fun assertLocationPoints(expected: Point, gotten: Point) {
        Assert.assertEquals(expected.latitude, gotten.latitude, 0.0)
        Assert.assertEquals(expected.longitude, gotten.longitude, 0.0)
    }
}