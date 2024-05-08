package com.qubacy.geoqq.ui.application.activity._common.screen.login

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.databinding.FragmentLoginBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.LoginViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.module.LoginViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state.LoginUiState
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.loading.model.operation.SetLoadingStateUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.factory._test.mock.LoginViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.module.FakeLoginViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.operation.SignInUiOperation
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(LoginViewModelModule::class)
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest : BusinessFragmentTest<
    FragmentLoginBinding, LoginUiState, LoginViewModel, LoginViewModelMockContext, LoginFragment
>() {
    override fun createDefaultViewModelMockContext(): LoginViewModelMockContext {
        return LoginViewModelMockContext(LoginUiState())
    }

    override fun attachViewModelMockContext() {
        FakeLoginViewModelModule.mockContext = mViewModelMockContext
    }

    override fun getFragmentClass(): Class<LoginFragment> {
        return LoginFragment::class.java
    }

    override fun getCurrentDestination(): Int {
        return R.id.loginFragment
    }

    @Test
    fun tryingToSignInWithLocalTokenOnEnteringFragmentTest() {
        defaultInit()

        Assert.assertTrue(mViewModelMockContext.signInWithTokenCallFlag)
    }

    @Test
    fun clickingSignInWithoutLoginDataLeadsToShowingErrorTest() {
        defaultInit()

        Espresso.onView(withId(R.id.fragment_login_button_login))
            .perform(ViewActions.click())
        Espresso.onView(withText(R.string.fragment_login_input_error_login))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(R.string.fragment_input_error_password))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickingSignInWithLoginLeadsToShowingErrorTest() {
        defaultInit()

        val login = "testtest"

        Espresso.onView(withId(R.id.fragment_login_text_input_login))
            .perform(ViewActions.typeText(login), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_button_login))
            .perform(ViewActions.click())
        Espresso.onView(withText(R.string.fragment_input_error_password))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickingSignInWithPasswordLeadsToShowingErrorTest() {
        defaultInit()

        val password = "password"

        Espresso.onView(withId(R.id.fragment_login_text_input_password))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_button_login))
            .perform(ViewActions.click())
        Espresso.onView(withText(R.string.fragment_login_input_error_login))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickingSignInWithFullLoginDataLeadsToSuccessfulSigningInTest() {
        defaultInit()

        val login = "testtest"
        val password = "password"

        Espresso.onView(withId(R.id.fragment_login_text_input_login))
            .perform(ViewActions.typeText(login), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_text_input_password))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_button_login))
            .perform(ViewActions.click())

        Assert.assertTrue(mViewModelMockContext.signInWithLoginDataCallFlag)

        Espresso.onView(withText(R.string.component_error_dialog_title_text))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun clickingChangeLoginModeLeadsToShowingAndHidingRepeatPasswordTest() {
        defaultInit()

        Espresso.onView(withId(R.id.fragment_login_text_change_login_type))
            .check(ViewAssertions.matches(
                withText(R.string.fragment_login_text_change_login_type_text_sign_in_mode)))
        Espresso.onView(withId(R.id.fragment_login_button_change_login_type))
            .check(ViewAssertions.matches(
                withText(R.string.fragment_login_button_change_login_type_text_sign_in_mode)))

        Espresso.onView(withId(R.id.fragment_login_button_change_login_type))
            .perform(ViewActions.click())

        Assert.assertTrue(mViewModelMockContext.setLoginModeCallFlag)

        Espresso.onView(withId(R.id.fragment_login_text_input_repeat_password_wrapper))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.fragment_login_text_change_login_type))
            .check(ViewAssertions.matches(
                withText(R.string.fragment_login_text_change_login_type_text_sign_up_mode)))
        Espresso.onView(withId(R.id.fragment_login_button_change_login_type))
            .check(ViewAssertions.matches(
                withText(R.string.fragment_login_button_change_login_type_text_sign_up_mode)))
    }

    @Test
    fun clickingSignUpWithDifferentPasswordsLeadsToShowingErrorTest() {
        defaultInit()

        Espresso.onView(withId(R.id.fragment_login_button_change_login_type))
            .perform(ViewActions.click())

        val login = "testtest"
        val password = "password"
        val repeatPassword = "passwordddd"
        val expectedError = TestError.normal

        mViewModelMockContext.retrieveErrorResult = expectedError

        Espresso.onView(withId(R.id.fragment_login_text_input_login))
            .perform(ViewActions.typeText(login), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_text_input_password))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_text_input_repeat_password))
            .perform(ViewActions.typeText(repeatPassword), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_button_login))
            .perform(ViewActions.click())
        Espresso.onView(withText(expectedError.message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun clickingSignUpWithFullSigningUpDataLeadsToSuccessfulSigningUpTest() {
        defaultInit()

        Espresso.onView(withId(R.id.fragment_login_button_change_login_type))
            .perform(ViewActions.click())

        Assert.assertTrue(mViewModelMockContext.setLoginModeCallFlag)

        val login = "testtest"
        val password = "password"
        val repeatPassword = password

        Espresso.onView(withId(R.id.fragment_login_text_input_login))
            .perform(ViewActions.typeText(login), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_text_input_password))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_text_input_repeat_password))
            .perform(ViewActions.typeText(repeatPassword), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_button_login))
            .perform(ViewActions.click())

        Assert.assertTrue(mViewModelMockContext.signUpCallFlag)

        Espresso.onView(withText(R.string.component_error_dialog_title_text))
            .check(ViewAssertions.doesNotExist())

        // todo: implement later:
        //val currentDestination = mNavController.currentDestination

        //Assert.assertEquals(R.id., currentDestination)
    }

    @Test
    fun inputsClearedAfterSuccessfulSigningInTest() {
        defaultInit()

        val login = "testtest"
        val password = "password"

        Espresso.onView(withId(R.id.fragment_login_text_input_login))
            .perform(ViewActions.typeText(login), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_text_input_password))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_button_login))
            .perform(ViewActions.click())

        Espresso.onView(withId(R.id.fragment_login_text_input_login))
            .check(ViewAssertions.matches(withText(String())))
        Espresso.onView(withId(R.id.fragment_login_text_input_password))
            .check(ViewAssertions.matches(withText(String())))
    }

    @Test
    fun inputsClearedAfterSuccessfulSigningUpTest() {
        val initUiState = LoginUiState(loginMode = LoginUiState.LoginMode.SIGN_UP)

        initWithModelContext(LoginViewModelMockContext(initUiState))

        val login = "testtest"
        val password = "password"

        Espresso.onView(withId(R.id.fragment_login_text_input_login))
            .perform(ViewActions.typeText(login), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_text_input_password))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_text_input_repeat_password))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(withId(R.id.fragment_login_button_login))
            .perform(ViewActions.click())

        Espresso.onView(withId(R.id.fragment_login_text_input_login))
            .check(ViewAssertions.matches(withText(String())))
        Espresso.onView(withId(R.id.fragment_login_text_input_password))
            .check(ViewAssertions.matches(withText(String())))
        Espresso.onView(withId(R.id.fragment_login_text_input_repeat_password))
            .check(ViewAssertions.matches(withText(String())))
    }

    @Deprecated("Poorly synchronized so can fail.")
    @Test
    fun processSignInOperationTest() = runTest {
        val signInOperation = SignInUiOperation()

        val expectedDestination = R.id.mateChatsFragment

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(signInOperation)

        val currentDestination = mNavController.currentDestination!!.id

        Assert.assertEquals(expectedDestination, currentDestination)
    }

    @Test
    fun processSetLoadingOperationTest() = runTest {
        val initUiState = LoginUiState(isLoading = false)

        val setLoadingStateOperation = SetLoadingStateUiOperation(true)

        initWithModelContext(LoginViewModelMockContext(initUiState))

        mViewModelMockContext.uiOperationFlow.emit(setLoadingStateOperation)

        Espresso.onView(withId(R.id.fragment_login_text_input_login))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
        Espresso.onView(withId(R.id.fragment_login_text_input_password))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
        Espresso.onView(withId(R.id.fragment_login_text_input_repeat_password))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
        Espresso.onView(withId(R.id.fragment_login_button_login))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
        Espresso.onView(withId(R.id.fragment_login_button_change_login_type))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }
}