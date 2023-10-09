package com.qubacy.geoqq.ui.screen.mate.chat

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui.util.MaterialTextInputVisualLineCountViewAssertion
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MateChatFragmentTest {
    private lateinit var mMateChatFragmentScenarioRule: FragmentScenario<MateChatFragment>

    @Before
    fun setup() {
        mMateChatFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_Mates)
    }

    @Test
    fun allElementsInPlaceTest() {
        Espresso.onView(ViewMatchers.withId(R.id.chat_recycler_view))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.sending_button))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun allElementsEnabledTest() {
        Espresso.onView(ViewMatchers.withId(R.id.chat_recycler_view))
            .perform(ViewActions.swipeDown())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(ViewMatchers.withId(R.id.sending_button))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun clickingSendButtonWithEmptyMessageFieldLeadsToErrorTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .perform(ViewActions.clearText())
        Espresso.onView(ViewMatchers.withId(R.id.sending_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.error_chat_message_incorrect))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun emptyMessageSnackbarDisappearsOnDismissButtonClickedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .perform(ViewActions.clearText())
        Espresso.onView(ViewMatchers.withId(R.id.sending_button))
            .perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withText(R.string.fragment_base_show_message_action_dismiss_text))
            .perform(ViewActions.click())
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun clickingSendButtonWithNotEmptyMessageFieldCleansItAndGoesWithoutErrorTest() {
        val messageText = "Hi there"

        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .perform(ViewActions.typeText(messageText))
        Espresso.onView(ViewMatchers.withId(R.id.sending_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .check(ViewAssertions.matches(ViewMatchers.withText(String())))
        Espresso.onView(ViewMatchers.withText(R.string.error_chat_message_incorrect))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun messageLongTextFitsTwoVisualLinesTest() {
        val longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur."

        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .perform(ViewActions.typeText(longText))
            .check(MaterialTextInputVisualLineCountViewAssertion(2))
    }
}