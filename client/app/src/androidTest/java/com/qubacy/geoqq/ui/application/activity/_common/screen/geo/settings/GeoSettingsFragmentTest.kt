package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings

import android.Manifest
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.BuildConfig
import com.qubacy.geoqq.databinding.FragmentGeoSettingsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.StatefulFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.GeoSettingsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.factory._test.mock.GeoSettingsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.module.FakeGeoSettingsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.state.GeoSettingsUiState
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui._common._test.view.util.action.pinch.PinchZoomViewAction
import com.qubacy.geoqq.ui._common._test.view.util.action.wait.WaitViewAction
import com.qubacy.geoqq.ui._common._test.view.util.assertion.mapview.changed.MapViewChangedViewAssertion
import com.qubacy.geoqq.ui._common._test.view.util.assertion.mapview.loaded.MapViewLoadedViewAssertion
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.component.hint.view.HintViewProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.location.model.operation.LocationPointChangedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.component.map.view.GeoMapView
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.module.GeoSettingsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.operation.ChangeRadiusUiOperation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.VisibleRegion
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(GeoSettingsViewModelModule::class)
@RunWith(AndroidJUnit4::class)
class GeoSettingsFragmentTest(

) : StatefulFragmentTest<
    FragmentGeoSettingsBinding,
    GeoSettingsUiState,
    GeoSettingsViewModel,
    GeoSettingsViewModelMockContext,
    GeoSettingsFragment
>() {
    companion object {
        const val DEFAULT_MAX_MAP_LOADING_DURATION = 10000L

        val DEFAULT_LOCATION_POINT = Point(56.010543, 92.852581)

        @JvmStatic
        @BeforeClass
        fun overallSetup() {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
        }
    }

    override fun getPermissionsToGrant(): Array<String> {
        return arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun createDefaultViewModelMockContext(): GeoSettingsViewModelMockContext {
        return GeoSettingsViewModelMockContext(
            GeoSettingsUiState(
            radius = GeoSettingsViewModel.DEFAULT_RADIUS_METERS)
        )
    }

    override fun attachViewModelMockContext() {
        FakeGeoSettingsViewModelModule.mockContext = mViewModelMockContext
    }

    override fun getFragmentClass(): Class<GeoSettingsFragment> {
        return GeoSettingsFragment::class.java
    }

    override fun getCurrentDestination(): Int {
        return R.id.geoSettingsFragment
    }

    /**
     * Note: this one is poorly synchronized:
     */
    @Test
    fun hintTextAppearsForShortTimeThenDisappearsTest() {
        defaultInit()

        Espresso.onView(isRoot())
            .perform(WaitViewAction(GeoSettingsFragment.HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT))
        Espresso.onView(withId(R.id.component_hint_text))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        val disappearanceTimeout = HintViewProvider.DEFAULT_HINT_TEXT_ANIMATION_DISAPPEARANCE_TIMEOUT +
                HintViewProvider.DEFAULT_APPEARANCE_ANIMATION_DURATION

        Espresso.onView(isRoot())
            .perform(WaitViewAction(disappearanceTimeout))
        Espresso.onView(withId(R.id.component_hint_text))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun mapLoadsInTenSecondsSecondsOnFragmentStartTest() = runTest {
        val timeToLoad = 10000L

        defaultInit()

        Espresso.onView(withId(R.id.fragment_geo_settings_map))
            .perform(WaitViewAction(timeToLoad))
            .check(MapViewLoadedViewAssertion())
    }

    @Test
    fun clickingMyProfileMenuOptionLeadsToNavigationToMyProfileFragmentTest() {
        defaultInit()

        val expectedDestination = R.id.myProfileFragment

        Espresso.onView(withId(R.id.main_top_bar_option_my_profile)).perform(ViewActions.click())

        val gottenDestination = mNavController.currentDestination!!.id

        Assert.assertEquals(expectedDestination, gottenDestination)
    }

    @Test
    fun clickingInfoMenuOptionLeadsToShowingHintTest() {
        defaultInit()

        val hintVisibilityTime = GeoSettingsFragment.HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT +
            HintViewProvider.DEFAULT_APPEARANCE_ANIMATION_DURATION +
            HintViewProvider.DEFAULT_HINT_TEXT_ANIMATION_DISAPPEARANCE_TIMEOUT +
            HintViewProvider.DEFAULT_APPEARANCE_ANIMATION_DURATION

        Espresso.onView(isRoot()).perform(WaitViewAction(hintVisibilityTime))

        Espresso.onView(withId(R.id.geo_settings_top_bar_option_hint)).perform(ViewActions.click())

        Espresso.onView(isRoot())
            .perform(WaitViewAction(GeoSettingsFragment.HINT_TEXT_ANIMATION_APPEARANCE_TIMEOUT))
        Espresso.onView(withId(R.id.component_hint_text))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun mapLoadingEndsWithUsingViewModelMethodTest() {
        defaultInit()

        Espresso.onView(isRoot()).perform(WaitViewAction(DEFAULT_MAX_MAP_LOADING_DURATION))

        Assert.assertTrue(mViewModelMockContext.setMapLoadingStatusCallFlag)
    }

    @Test
    fun appGettingNewLocationLeadsToUsingViewModelMethodTest() {
        defaultInit()

        Espresso.onView(isRoot()).perform(WaitViewAction(1000))

        Assert.assertTrue(mViewModelMockContext.changeLastLocationCallFlag)
    }

    @Test
    fun performingPinchZoomLeadsToUsingViewModelMethodTest() {
        defaultInit()

        Espresso.onView(withId(R.id.fragment_geo_settings_map))
            .perform(PinchZoomViewAction())

        Assert.assertTrue(mViewModelMockContext.applyScaleForRadiusCallFlag)
    }

    /**
     * Synchronization is TOO bad in this one:
     */
    @Test
    fun processChangeRadiusUiOperationTest() = runTest {
        val initLocationPoint = DEFAULT_LOCATION_POINT
        val initRadius = GeoSettingsViewModel.DEFAULT_RADIUS_METERS
        val initUiState = GeoSettingsUiState(
            lastLocationPoint = initLocationPoint,
            radius = initRadius
        )
        val initLocationPointChangedUiOperation = LocationPointChangedUiOperation(initLocationPoint)

        val radius = (initRadius * 1.5).toInt()
        val changeRadiusUiOperation = ChangeRadiusUiOperation(radius)

        val expectedRadiusText = InstrumentationRegistry.getInstrumentation().targetContext
            .getString(R.string.fragment_geo_settings_text_radius_text, radius.toInt().toString())

        initWithModelContext(GeoSettingsViewModelMockContext(initUiState))

        Espresso.onView(isRoot()).perform(WaitViewAction(DEFAULT_MAX_MAP_LOADING_DURATION))

        mViewModelMockContext.uiOperationFlow.emit(initLocationPointChangedUiOperation)

        val mapView = retrieveMapView()
        lateinit var prevVisibleRegion: VisibleRegion

        mActivityScenario.onActivity {
            prevVisibleRegion = mapView.mapWindow.map.visibleRegion
        }

        mViewModelMockContext.uiOperationFlow.emit(changeRadiusUiOperation)

        Espresso.onView(withId(R.id.fragment_geo_settings_map))
            .perform(WaitViewAction(1000))
            .check(MapViewChangedViewAssertion(prevVisibleRegion))
        Espresso.onView(withText(expectedRadiusText))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun processSetLoadingStateUiOperationTest() = runTest {
        val initLoadingState = false
        val initUiState = GeoSettingsUiState(
            isLoading = initLoadingState,
            radius = GeoSettingsViewModel.DEFAULT_RADIUS_METERS
        )

        val loadingState = true
        val setLoadingStateUiOperation = SetLoadingStateUiOperation(loadingState)

        initWithModelContext(GeoSettingsViewModelMockContext(initUiState))

        Espresso.onView(withId(R.id.fragment_geo_settings_progress_bar))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        mViewModelMockContext.uiOperationFlow.emit(setLoadingStateUiOperation)

        Espresso.onView(withId(R.id.fragment_geo_settings_progress_bar))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun processLocationPointChangedUiOperationTest() = runTest {
        val initLocationPoint = DEFAULT_LOCATION_POINT
        val initRadius = GeoSettingsViewModel.DEFAULT_RADIUS_METERS
        val initUiState = GeoSettingsUiState(
            lastLocationPoint = initLocationPoint,
            radius = initRadius
        )
        val initLocationPointChangedUiOperation = LocationPointChangedUiOperation(initLocationPoint)

        val locationPoint = Point(
            initLocationPoint.latitude.plus(1),
            initLocationPoint.longitude.plus(1)
        )
        val locationPointChangedUiOperation = LocationPointChangedUiOperation(locationPoint)

        initWithModelContext(GeoSettingsViewModelMockContext(initUiState))

        Espresso.onView(isRoot()).perform(WaitViewAction(DEFAULT_MAX_MAP_LOADING_DURATION))

        mViewModelMockContext.uiOperationFlow.emit(initLocationPointChangedUiOperation)

        val mapView = retrieveMapView()
        lateinit var prevVisibleRegion: VisibleRegion

        mActivityScenario.onActivity {
            prevVisibleRegion = mapView.mapWindow.map.visibleRegion
        }

        mViewModelMockContext.uiOperationFlow.emit(locationPointChangedUiOperation)

        Espresso.onView(withId(R.id.fragment_geo_settings_map))
            .perform(WaitViewAction(1000))
            .check(MapViewChangedViewAssertion(prevVisibleRegion))
    }

    private fun retrieveMapView(): GeoMapView {
        val binding = BaseFragment::class.java.getDeclaredField("mBinding")
            .apply { isAccessible = true }.get(mFragment) as FragmentGeoSettingsBinding

        return binding.fragmentGeoSettingsMap
    }
}