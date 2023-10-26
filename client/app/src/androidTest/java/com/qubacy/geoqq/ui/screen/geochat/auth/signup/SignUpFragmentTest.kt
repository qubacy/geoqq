package com.qubacy.geoqq.ui.screen.geochat.auth.signup

import android.view.View
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputEditText
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.data.geochat.auth.common.state.AuthState
import com.qubacy.geoqq.ui.screen.geochat.auth.common.AuthFragmentTest
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class SignUpFragmentTest : AuthFragmentTest() {
    private lateinit var mSignUpFragmentScenarioRule: FragmentScenario<SignUpFragment>
    private lateinit var mModel: SignUpViewModel
    private lateinit var mNavController: TestNavHostController

    private lateinit var mSignUpUiStateTestData: AuthUiStateTestData

    @Before
    fun setup() {
        mSignUpFragmentScenarioRule =
            launchFragmentInContainer<SignUpFragment>(themeResId = R.style.Theme_Geoqq_GeoChat)
        mSignUpFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        mNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        mSignUpFragmentScenarioRule.onFragment {
            mNavController.setGraph(R.navigation.nav_graph)
            mNavController.setCurrentDestination(R.id.signUpFragment)
            Navigation.setViewNavController(it.requireView(), mNavController)

            mModel = ViewModelProvider(it)[SignUpViewModel::class.java]
        }

        val authStateFlowFieldReflection = SignInViewModel::class.java.superclass
            .getDeclaredField("mAuthStateFlow").apply {
                isAccessible = true
            }

        mSignUpUiStateTestData = AuthUiStateTestData(
            mModel,
            authStateFlowFieldReflection.get(mModel) as MutableStateFlow<AuthState>
        )
    }

    @Test
    fun allElementsAreInPlaceTest() {
        Espresso.onView(ViewMatchers.withId(R.id.login_input))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.password_input))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.password_confirmation_input))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .check(
                ViewAssertions.matches(
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
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_confirmation_input)),
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
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_confirmation_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(ViewActions.typeText(longText))
            .check(SingleLineMaterialTextInputViewAssertion())
    }

    @Test
    fun buttonIsEnabledTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun nothingProvidedAndConfirmClickedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                ViewMatchers.withText(R.string.error_sign_up_data_incorrect)
            )))
    }

    @Test
    fun onlyLoginProvidedAndConfirmClickedTest() {
        val login = "loginnnn"

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.login_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(login), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                ViewMatchers.withText(R.string.error_sign_up_data_incorrect)
            )))
    }

    @Test
    fun onlyPasswordProvidedAndConfirmClickedTest() {
        val password = "password"

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                ViewMatchers.withText(R.string.error_sign_up_data_incorrect)
            )))
    }

    @Test
    fun onlyRepeatedPasswordProvidedAndConfirmClickedTest() {
        val password = "password"

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_confirmation_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(R.string.error_sign_up_data_incorrect)
                    )))
    }

    @Test
    fun loginAndPasswordProvidedAndConfirmClickedTest() {
        val login = "loginnnn"
        val password = "password"

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
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(R.string.error_sign_up_data_incorrect)
                    )))
    }

    @Test
    fun loginAndRepeatedPasswordProvidedAndConfirmClickedTest() {
        val login = "loginnnn"
        val password = "password"

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.login_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(login), ViewActions.closeSoftKeyboard())
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_confirmation_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(R.string.error_sign_up_data_incorrect)
                    )))
    }

    @Test
    fun passwordAndRepeatedPasswordProvidedAndConfirmClickedTest() {
        val password = "password"

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(password))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_confirmation_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(
                ViewAssertions.matches(
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(R.string.error_sign_up_data_incorrect)
                    )))
    }


    @Test
    fun correctLoginAndPasswordAndRepeatedPasswordProvidedAndConfirmClickLeadsToShowingLoadingScreenAndThenMovingToMainMenuTest() {
        val login = "loginnnn"
        val password = "password"

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
            .perform(ViewActions.typeText(login))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_confirmation_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(ViewAssertions.matches(
                ViewMatchers.hasDescendant(ViewMatchers.withId(R.id.loading_screen))))

        mSignUpFragmentScenarioRule.onFragment {
            mSignUpUiStateTestData.setAuthorized()
        }

        Assert.assertEquals(R.id.mainMenuFragment, mNavController.currentDestination?.id)
    }

    @Test
    fun loginAndPasswordAndRepeatedPasswordProvidedAndConfirmClickedThenAbortedByBackButtonClickTest() {
        val login = "loginnnn"
        val password = "password"

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
            .perform(ViewActions.typeText(password))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_confirmation_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .perform(ViewActions.pressBack())
        Espresso.onView(ViewMatchers.withId(R.id.loading_screen))
            .check(ViewAssertions.doesNotExist())
    }
    @Test
    fun loginAndPasswordAndRepeatedPasswordProvidedAndConfirmClickedThenAbortedByLoadingScreenClickTest() {
        val login = "loginnnn"
        val password = "password"

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
            .perform(ViewActions.typeText(password))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_confirmation_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.loading_screen))
            .perform(ViewActions.click())
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun errorMessageShowsUpOnSettingUiStateWithErrorTest() {
        val error = Error(R.string.error_sign_up_failed, Error.Level.NORMAL)

        mSignUpFragmentScenarioRule.onFragment {
            mSignUpUiStateTestData.showError(error)
        }

        Espresso.onView(withText(error.messageResId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun criticalErrorLeadsToClosingAppTest() {
        val error = Error(R.string.error_sign_up_failed, Error.Level.CRITICAL)

        mSignUpFragmentScenarioRule.onFragment {
            mSignUpUiStateTestData.showError(error)
        }

        try {
            Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
                .perform(ViewActions.click())

        } catch (e: Exception) {
            Assert.assertEquals(NoActivityResumedException::class, e::class)
        }
    }
}