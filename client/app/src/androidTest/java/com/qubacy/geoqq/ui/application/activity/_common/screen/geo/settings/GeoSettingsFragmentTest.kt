package com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings

import android.Manifest
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.UiSelector
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
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.component.map.view.GeoMapView
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.settings.model.module.GeoSettingsViewModelModule
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.VisibleRegion
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.junit.Assert
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

    override fun setup() {
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)

        super.setup()
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

    /**
     * Synchronization is TOO bad in this one:
     */
    @Test
    fun performingMapPinchZoomingLeadsToMapZoomingTest() {
        val pinchZoomPercent = 50

        defaultInit()

        Espresso.onView(isRoot()).perform(WaitViewAction(DEFAULT_MAX_MAP_LOADING_DURATION))

        val mapView = retrieveMapView()
        lateinit var prevVisibleRegion: VisibleRegion

        mActivityScenario.onActivity {
            prevVisibleRegion = mapView.mapWindow.map.visibleRegion
        }

        PinchZoomViewAction.perform(
            UiSelector().className(GeoMapView::class.java), pinchZoomPercent)

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