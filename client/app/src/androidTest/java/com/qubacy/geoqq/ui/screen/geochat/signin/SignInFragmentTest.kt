package com.qubacy.geoqq.ui.screen.geochat.signin

import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputEditText
import com.qubacy.geoqq.R
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInFragmentTest {
    private lateinit var mSignInFragmentScenarioRule: FragmentScenario<SignInFragment>

    @Before
    fun setup() {
        mSignInFragmentScenarioRule =
            launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_Geoqq_GeoChat)
    }

    @Test
    fun allElementsAreInPlaceTest() {
        mSignInFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        Espresso.onView(ViewMatchers.withId(R.id.login_input))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.password_input))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun textInputsAreEnabledTest() {
        val text = "something"

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.login_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(ViewActions.typeText(text))
            .check(ViewAssertions.matches(ViewMatchers.withText(text)))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(ViewActions.typeText(text))
            .check(ViewAssertions.matches(ViewMatchers.withText(text)))
    }

    class SingleLineMaterialTextInputViewAssertion() : ViewAssertion {
        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
            if (view == null)
                throw NoMatchingViewException.Builder().build()
            if (view !is TextInputEditText)
                throw NoMatchingViewException.Builder().build()

            val materialTextInput = view as TextInputEditText

            if (materialTextInput.lineCount != 1)
                throw NoMatchingViewException.Builder().build()
        }
    }

    @Test
    fun longTextIsSingleLineForTextInputsTest() {
        val longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur."

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.login_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(ViewActions.typeText(longText))
            .check(SingleLineMaterialTextInputViewAssertion())
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(ViewActions.typeText(longText))
            .check(SingleLineMaterialTextInputViewAssertion())
    }

    @Test
    fun buttonsAreEnabledTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun nothingProvidedAndSignInClickedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(
                ViewMatchers.withText(R.string.error_sign_in_data_not_full)
            )))
    }

    @Test
    fun onlyLoginProvidedAndSignInClickedTest() {
        val login = "login"

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.login_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(login), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(
                ViewMatchers.withText(R.string.error_sign_in_data_not_full)
            )))
    }

    @Test
    fun onlyPasswordProvidedAndSignInClickedTest() {
        val password = "pass"

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(
                ViewMatchers.withText(R.string.error_sign_in_data_not_full)
            )))
    }

    @Test
    fun loginAndPasswordProvidedAndSignInClickedTest() {
        val login = "login"
        val password = "pass"

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.login_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(login))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.error_sign_in_data_not_full))
            .check(ViewAssertions.doesNotExist())
    }
}