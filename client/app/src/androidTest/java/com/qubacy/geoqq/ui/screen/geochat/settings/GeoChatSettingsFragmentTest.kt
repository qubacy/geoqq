package com.qubacy.geoqq.ui.screen.geochat.settings

import android.location.Location
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.R
import com.qubacy.geoqq.data.geochat.settings.GeoChatSettingsContext
import com.qubacy.geoqq.databinding.FragmentGeoChatSettingsBinding
import com.qubacy.geoqq.ui.common.fragment.location.model.LocationViewModel
import com.qubacy.geoqq.ui.screen.geochat.settings.model.GeoChatSettingsViewModel
import com.qubacy.geoqq.ui.util.SilentClickViewAction
import com.qubacy.geoqq.ui.util.WaitingViewAction
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CircleMapObject
import com.yandex.mapkit.map.ClusterizedPlacemarkCollection
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectVisitor
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolygonMapObject
import com.yandex.mapkit.map.PolylineMapObject
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Field

@RunWith(AndroidJUnit4::class)
class GeoChatSettingsFragmentTest {
    class LocationTestData(
        private val mCurLocationCircleFieldReflection: Field,
        private val mFragment: GeoChatSettingsFragment,
        private val mModel: GeoChatSettingsViewModel)
    {

        fun getCurLocationPoint(): Point? {
            return mModel.lastLocationPoint.value
        }

        fun setLocation(
            location: Location,
            fragmentScenario: FragmentScenario<GeoChatSettingsFragment>)
        {
            fragmentScenario.onFragment {
                mModel.changeLastLocation(location)
            }
        }

        fun getCurLocationCircle(): CircleMapObject {
            return mCurLocationCircleFieldReflection.get(mFragment) as CircleMapObject
        }
    }

    private lateinit var mSettingsFragmentScenarioRule: FragmentScenario<GeoChatSettingsFragment>
    private lateinit var mSettingsBinding: FragmentGeoChatSettingsBinding
    private lateinit var mNavHostController: TestNavHostController

    private lateinit var mLocationTestData: LocationTestData

    @Before
    fun setup() {
        mSettingsFragmentScenarioRule =
            launchFragmentInContainer<GeoChatSettingsFragment>(themeResId = R.style.Theme_Geoqq_GeoChat)
        mSettingsFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        var fragment: GeoChatSettingsFragment? = null
        var model: GeoChatSettingsViewModel? = null

        mNavHostController = TestNavHostController(ApplicationProvider.getApplicationContext())

        mSettingsFragmentScenarioRule.onFragment {
            mNavHostController.setGraph(R.navigation.nav_graph)
            mNavHostController.setCurrentDestination(R.id.geoChatSettingsFragment)
            Navigation.setViewNavController(it.requireView(), mNavHostController)

            fragment = it
            model = ViewModelProvider(it)[GeoChatSettingsViewModel::class.java]

            mSettingsBinding = DataBindingUtil.getBinding<FragmentGeoChatSettingsBinding>(it.view!!)!!
        }

        val curLocationCircleFieldReflection =
            GeoChatSettingsFragment::class.java.getDeclaredField("mCurLocationCircle")
                .apply { isAccessible = true }

        mLocationTestData = LocationTestData(
            curLocationCircleFieldReflection,
            fragment!!,
            model!!
        )
    }

    @Test
    fun allElementsAreInPlaceTest() {
        Espresso.onView(ViewMatchers.withId(R.id.map))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.radius_settings))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.radius_setting_1))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.radius_setting_2))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.radius_setting_3))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.radius_setting_4))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.radius_setting_5))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.go_button))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun allButtonsEnabledTest() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.radius_setting_1)),
            withId(R.id.radio_button)
        ))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.radius_setting_2)),
            withId(R.id.radio_button)
        ))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.radius_setting_3)),
            withId(R.id.radio_button)
        ))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.radius_setting_4)),
            withId(R.id.radio_button)
        ))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(withId(R.id.radius_setting_5)),
            withId(R.id.radio_button)
        ))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun changingChosenRadiusTo500mByClickingRadiusOptionButtonsTest() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_2))
        ))
            .perform(WaitingViewAction(2000), ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_1))
        ))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_3))
        ))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_4))
        ))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_5))
        ))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
    }

    @Test
    fun changingChosenRadiusTo10kmByClickingRadiusOptionButtonsTest() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_5))))
            .perform(WaitingViewAction(2000), ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_1))))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_2))))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_3))))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_4))))
            .check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
    }

    @Test
    fun locationPointAndCircleAreInitializedAfterSuccessfulStartHavingPermissionsTest() {
        assertNotEquals(Point(0.0, 0.0), mLocationTestData.getCurLocationPoint())
        assertNotNull(mLocationTestData.getCurLocationCircle())
    }

    @Test
    fun curLocationCircleChangesOnMapOnRadiusOptionChangingTest() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_2))))
            .perform(WaitingViewAction(2000), ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        val prevLocationCircle = mLocationTestData.getCurLocationCircle()

        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_5))))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        val curLocationCircle = mLocationTestData.getCurLocationCircle()

        assertNotEquals(prevLocationCircle, curLocationCircle)

        var lastLocationCircle: CircleMapObject? = null

        mSettingsFragmentScenarioRule.onFragment {
            mSettingsBinding.map.mapWindow.map.mapObjects.traverse(object : MapObjectVisitor {
                override fun onPlacemarkVisited(p0: PlacemarkMapObject) {}
                override fun onPolylineVisited(p0: PolylineMapObject) {}
                override fun onPolygonVisited(p0: PolygonMapObject) {}
                override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {}
                override fun onCollectionVisitEnd(p0: MapObjectCollection) {}

                override fun onCircleVisited(p0: CircleMapObject) {
                    lastLocationCircle = p0
                }

                override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean {
                    return true
                }

                override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean {
                    return true
                }
            })
        }

        assertNotNull(lastLocationCircle)
        assertEquals(curLocationCircle, lastLocationCircle)
    }

    @Test
    fun cameraChangesItsBoundsOnLocationCircleChangingTest() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_1))))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        var prevCameraPosition: CameraPosition = CameraPosition()

        mSettingsFragmentScenarioRule.onFragment {
            prevCameraPosition = mSettingsBinding.map.mapWindow.map.cameraPosition
        }

        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_5))))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        var curCameraPosition: CameraPosition = CameraPosition()

        mSettingsFragmentScenarioRule.onFragment {
            curCameraPosition = mSettingsBinding.map.mapWindow.map.cameraPosition
        }

        assertNotEquals(prevCameraPosition, curCameraPosition)
    }

    @Test
    fun radiusChangeTo10kmLeadsToLocationCircleRadiusChangingTo10kmTest() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_1))))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        val prevLocationCircle = mLocationTestData.getCurLocationCircle()

        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_5))))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        val curLocationCircle = mLocationTestData.getCurLocationCircle()

        assertNotNull(curLocationCircle)
        assertNotEquals(prevLocationCircle, curLocationCircle)

        var curLocationCircleRadius: Float = 0f

        mSettingsFragmentScenarioRule.onFragment {
            curLocationCircleRadius = curLocationCircle.geometry.radius
        }

        assertEquals(
            GeoChatSettingsContext.RADIUS_OPTION_IN_METERS_ARRAY.last(),
            curLocationCircleRadius)
    }

    // DOESN't work correctly; idk what to do for now
    // todo: try to have this case working:
    @Test
    fun changingLocationLeadsToLocationCircleChangingTest() {
        Espresso.onView(isRoot()).perform(WaitingViewAction(2000))

        val sourceLocation = Location("").apply {
            latitude = 46.000
            longitude = 56.000
        }

        mLocationTestData.setLocation(sourceLocation, mSettingsFragmentScenarioRule)
        Espresso.onView(isRoot()).perform(WaitingViewAction(2000)) // todo: delete;

        val gottenSourceLocationPoint = mLocationTestData.getCurLocationPoint()!!

        Assert.assertEquals(sourceLocation.latitude, gottenSourceLocationPoint.latitude, 0.001)
        Assert.assertEquals(sourceLocation.longitude, gottenSourceLocationPoint.longitude, 0.001)

        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.radio_button),
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.radius_setting_1))))
            .perform(WaitingViewAction(2000), ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isChecked()))

        val prevLocationCircle = mLocationTestData.getCurLocationCircle()
        val newLocation = Location("").apply {
            latitude = 47.000
            longitude = 57.000
        }

        mLocationTestData.setLocation(newLocation, mSettingsFragmentScenarioRule)

        val newLocationCircle = mLocationTestData.getCurLocationCircle()

        assertNotEquals(prevLocationCircle, newLocationCircle)
    }

    @Test
    fun goButtonClickLeadsToTransitionToGeoChatFragmentTest() {
        Espresso.onView(withId(R.id.go_button))
            .perform(WaitingViewAction(4000), SilentClickViewAction())

        Assert.assertEquals(R.id.geoChatFragment, mNavHostController.currentDestination?.id)
    }
}