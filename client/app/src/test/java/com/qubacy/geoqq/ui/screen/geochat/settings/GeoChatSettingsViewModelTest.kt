package com.qubacy.geoqq.ui.screen.geochat.settings

import com.qubacy.geoqq.data.geochat.settings.GeoChatSettingsContext
import com.qubacy.geoqq.domain.geochat.settings.GeoChatSettingsUseCase
import com.qubacy.geoqq.domain.geochat.settings.state.GeoChatSettingsState
import com.qubacy.geoqq.ui.screen.common.ViewModelTest
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModel
import com.qubacy.geoqq.ui.screen.geochat.settings.model.state.GeoChatSettingsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GeoChatSettingsViewModelTest : ViewModelTest() {
    private lateinit var mModel: GeoChatSettingsViewModel
    private lateinit var mGeoChatSettingsStateFlow: MutableStateFlow<GeoChatSettingsState?>

    private lateinit var mGeoChatSettingsUiStateFlow: Flow<GeoChatSettingsUiState?>

    private fun setNewUiState(newState: GeoChatSettingsState?) = runTest {
        if (newState == null) return@runTest

        mGeoChatSettingsStateFlow.emit(newState)
    }

    private fun initGeoChatSettingsViewModel(
        newState: GeoChatSettingsState? = null
    ) {
        val geoChatSettingsUseCaseMock = Mockito.mock(GeoChatSettingsUseCase::class.java)

        Mockito.`when`(geoChatSettingsUseCaseMock.getError(Mockito.anyLong()))
            .thenAnswer { setNewUiState(newState) }

        mGeoChatSettingsStateFlow = MutableStateFlow<GeoChatSettingsState?>(null)

        Mockito.`when`(geoChatSettingsUseCaseMock.stateFlow).thenAnswer {
            mGeoChatSettingsStateFlow
        }

        val mGeoChatSettingsUiStateFlowFieldReflection = GeoChatSettingsViewModel::class.java
            .getDeclaredField("mGeoChatSettingsUiStateFlow")
            .apply { isAccessible = true }

        mModel = GeoChatSettingsViewModel(geoChatSettingsUseCaseMock)
        mGeoChatSettingsUiStateFlow = mGeoChatSettingsUiStateFlowFieldReflection.get(mModel)
                as Flow<GeoChatSettingsUiState?>
    }

    @Before
    override fun setup() {
        super.setup()

        initGeoChatSettingsViewModel()
    }

    data class GetCurRadiusOptionMetersTestCase(
        val radiusOptionIndex: Int,
        val expectedRadiusOptionMeters: Int
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