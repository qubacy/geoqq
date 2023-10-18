package com.qubacy.geoqq.ui.screen.geochat.mainmenu

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui.util.SilentClickViewAction
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainMenuFragmentTest {
    private lateinit var mMainMenuFragmentScenarioRule: FragmentScenario<MainMenuFragment>

    private lateinit var mNavHostController: TestNavHostController

    @Before
    fun setup() {
        mMainMenuFragmentScenarioRule =
            launchFragmentInContainer<MainMenuFragment>(themeResId = R.style.Theme_Geoqq_GeoChat)
        mMainMenuFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        mNavHostController = TestNavHostController(ApplicationProvider.getApplicationContext())

        mMainMenuFragmentScenarioRule.onFragment {
            mNavHostController.setGraph(R.navigation.nav_graph)
            mNavHostController.setCurrentDestination(R.id.mainMenuFragment)
            Navigation.setViewNavController(it.requireView(), mNavHostController)
        }
    }

    @Test
    fun allElementsAreInPlaceTest() {
        Espresso.onView(ViewMatchers.withId(R.id.go_menu_option))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.mates_menu_option))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.profile_menu_option))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun menuOptionsAreEnabledTest() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.go_menu_option)),
            ViewMatchers.withId(R.id.menu_option_button)))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.mates_menu_option)),
            ViewMatchers.withId(R.id.menu_option_button)))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.profile_menu_option)),
            ViewMatchers.withId(R.id.menu_option_button)))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun goToChatOptionClickLeadsToTransitionToGeoChatSettingsFragmentTest() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.go_menu_option)),
            ViewMatchers.withId(R.id.menu_option_button)))
            .perform(SilentClickViewAction())

        Assert.assertEquals(R.id.geoChatSettingsFragment, mNavHostController.currentDestination?.id)
    }

    @Test
    fun myProfileOptionClickLeadsToTransitionToMyProfileFragmentTest() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.profile_menu_option)),
            ViewMatchers.withId(R.id.menu_option_button)))
            .perform(SilentClickViewAction())

        Assert.assertEquals(R.id.myProfileFragment, mNavHostController.currentDestination?.id)
    }

    @Test
    fun matesOptionClickLeadsToTransitionToMateChatsFragmentTest() {
        Espresso.onView(Matchers.allOf(
            ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.mates_menu_option)),
            ViewMatchers.withId(R.id.menu_option_button)))
            .perform(SilentClickViewAction())

        Assert.assertEquals(R.id.mateChatsFragment, mNavHostController.currentDestination?.id)
    }
}