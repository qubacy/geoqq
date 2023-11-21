package com.qubacy.geoqq.ui.screen.geochat.settings

import com.qubacy.geoqq.data.geochat.settings.GeoChatSettingsContext
import com.qubacy.geoqq.ui.screen.common.ViewModelTest
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GeoChatSettingsViewModelTest : ViewModelTest() {
    private lateinit var mModel: GeoChatSettingsViewModel

    @Before
    override fun setup() {
        super.setup()

        mModel = GeoChatSettingsViewModel()
    }

    data class GetCurRadiusOptionMetersTestCase(
        val radiusOptionIndex: Int,
        val expectedRadiusOptionMeters: Float
    )

    @Test
    fun getCurRadiusOptionMetersTest() {
        val testCases = listOf(
            GetCurRadiusOptionMetersTestCase(
                0, GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY[0]),
            GetCurRadiusOptionMetersTestCase(
                1, GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY[1]),
            GetCurRadiusOptionMetersTestCase(
                2, GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY[2]),
            GetCurRadiusOptionMetersTestCase(
                3, GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY[3]),
            GetCurRadiusOptionMetersTestCase(
                4, GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY[4]),
        )

        for (testCase in testCases) {
            mModel.changeCurRadiusOptionIndex(testCase.radiusOptionIndex)

            val curRadiusInMeters = mModel.getCurRadiusOptionMeters()

            Assert.assertEquals(testCase.expectedRadiusOptionMeters, curRadiusInMeters)
        }
    }

    @Test
    fun changeCurRadiusOptionIndexTest() {
        for (radiusOptionIndex in GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY.indices) {
            mModel.changeCurRadiusOptionIndex(radiusOptionIndex)
        }

        try {
            mModel.changeCurRadiusOptionIndex(GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY.size)

            throw IllegalStateException()

        } catch (e: Exception) { }
    }
}