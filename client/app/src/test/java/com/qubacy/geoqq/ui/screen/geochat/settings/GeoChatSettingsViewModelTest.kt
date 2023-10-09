package com.qubacy.geoqq.ui.screen.geochat.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GeoChatSettingsViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var mModel: GeoChatSettingsViewModel

    @Before
    fun setup() {
        mModel = GeoChatSettingsViewModel()

        mModel.curRadiusOptionIndex.observeForever {}
    }

    data class GetCurRadiusOptionMetersTestCase(
        val radiusOptionIndex: Int,
        val expectedRadiusOptionMeters: Float
    )

    @Test
    fun getCurRadiusOptionMetersTest() {
        val testCases = listOf(
            GetCurRadiusOptionMetersTestCase(
                0, GeoChatSettingsViewModel.RADIUS_OPTION_INDEX_TO_METERS_ARRAY[0]),
            GetCurRadiusOptionMetersTestCase(
                1, GeoChatSettingsViewModel.RADIUS_OPTION_INDEX_TO_METERS_ARRAY[1]),
            GetCurRadiusOptionMetersTestCase(
                2, GeoChatSettingsViewModel.RADIUS_OPTION_INDEX_TO_METERS_ARRAY[2]),
            GetCurRadiusOptionMetersTestCase(
                3, GeoChatSettingsViewModel.RADIUS_OPTION_INDEX_TO_METERS_ARRAY[3]),
            GetCurRadiusOptionMetersTestCase(
                4, GeoChatSettingsViewModel.RADIUS_OPTION_INDEX_TO_METERS_ARRAY[4]),
        )

        for (testCase in testCases) {
            mModel.changeCurRadiusOptionIndex(testCase.radiusOptionIndex)

            val curRadiusInMeters = mModel.getCurRadiusOptionMeters()

            Assert.assertEquals(testCase.expectedRadiusOptionMeters, curRadiusInMeters)
        }
    }
}