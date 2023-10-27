package com.qubacy.geoqq.ui.screen.geochat.auth.signin

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.textfield.TextInputEditText
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.ErrorBase
import com.qubacy.geoqq.common.error.local.LocalError
import com.qubacy.geoqq.data.common.auth.state.AuthState
import com.qubacy.geoqq.ui.screen.geochat.auth.common.AuthFragmentTest
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModel
import com.qubacy.geoqq.ui.util.MaterialTextInputVisualLineCountViewAssertion
import com.qubacy.geoqq.ui.util.SilentClickViewAction
import kotlinx.coroutines.flow.MutableStateFlow
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInFragmentTest : AuthFragmentTest() {
    private lateinit var mSignInFragmentScenarioRule: FragmentScenario<SignInFragment>
    private lateinit var mModel: SignInViewModel

    private lateinit var mSignInUiStateTestData: AuthUiStateTestData
    private lateinit var mNavController: TestNavHostController

    @Before
    fun setup() {
        mSignInFragmentScenarioRule =
            launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_Geoqq_GeoChat)
        mSignInFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        mNavController = TestNavHostController(ApplicationProvider.getApplicationContext())

        mSignInFragmentScenarioRule.onFragment {
            mNavController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(it.requireView(), mNavController)

            mModel = ViewModelProvider(it)[SignInViewModel::class.java]
        }

        val authStateFlowFieldReflection = SignInViewModel::class.java.superclass
            .getDeclaredField("mAuthStateFlow").apply {
                isAccessible = true
            }

        mSignInUiStateTestData = AuthUiStateTestData(
            mModel,
            authStateFlowFieldReflection.get(mModel) as MutableStateFlow<AuthState>
        )
    }

    @Test
    fun allElementsAreInPlaceTest() {
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
            .check(MaterialTextInputVisualLineCountViewAssertion(1))
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.password_input)),
                ViewMatchers.isAssignableFrom(TextInputEditText::class.java)
            ))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
            .perform(ViewActions.typeText(longText))
            .check(MaterialTextInputVisualLineCountViewAssertion(1))
    }

    @Test
    fun buttonsAreEnabledTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(ViewMatchers.withId(R.id.sign_up_button))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun nothingProvidedAndSignInClickedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(
                ViewMatchers.withText(R.string.error_sign_in_data_incorrect)
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
                ViewMatchers.withText(R.string.error_sign_in_data_incorrect)
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
                ViewMatchers.withText(R.string.error_sign_in_data_incorrect)
            )))
    }

    @Test
    fun correctLoginAndPasswordProvidedAndSignInClickLeadsToShowingLoadingAndThenMovingToMainMenuTest() {
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
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
            .perform(SilentClickViewAction())
        Espresso.onView(ViewMatchers.isRoot())
            .check(ViewAssertions.matches(
                ViewMatchers.hasDescendant(ViewMatchers.withId(R.id.loading_screen))))

        mSignInFragmentScenarioRule.onFragment {
            mSignInUiStateTestData.setAuthorized()
        }

        Assert.assertEquals(R.id.mainMenuFragment, mNavController.currentDestination?.id)
    }

    @Test
    fun loginAndPasswordProvidedAndSignInClickedThenAbortedByBackButtonClickTest() {
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
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot())
            .perform(ViewActions.pressBack())
        Espresso.onView(ViewMatchers.withId(R.id.loading_screen))
            .check(ViewAssertions.doesNotExist())
    }
    @Test
    fun loginAndPasswordProvidedAndSignInClickedThenAbortedByLoadingScreenClickTest() {
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
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.loading_screen))
            .perform(ViewActions.click())
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun errorMessageDisplayedOnErrorOccurredInUiStateTest() {
        val error = LocalError(R.string.error_sign_in_failed, ErrorBase.Level.NORMAL)

        mSignInFragmentScenarioRule.onFragment {
            mSignInUiStateTestData.showError(error)
        }

        Espresso.onView(withText(error.messageResId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun criticalErrorLeadsToClosingApp() {
        val error = LocalError(R.string.error_sign_in_failed, ErrorBase.Level.CRITICAL)

        mSignInFragmentScenarioRule.onFragment {
            mSignInUiStateTestData.showError(error)
        }

        try {
            Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
                .perform(ViewActions.click())

        } catch (e: Throwable) {
            Assert.assertEquals(NoActivityResumedException::class, e::class)
        }
    }

    @Test
    fun signUpButtonClickLeadsToTransitionToSignUpFragmentTest() {
        Espresso.onView(withId(R.id.sign_up_button))
            .perform(ViewActions.click())

        Assert.assertEquals(R.id.signUpFragment, mNavController.currentDestination?.id)
    }
}