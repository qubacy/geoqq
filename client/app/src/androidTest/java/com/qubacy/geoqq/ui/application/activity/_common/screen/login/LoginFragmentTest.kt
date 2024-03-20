package com.qubacy.geoqq.ui.application.activity._common.screen.login

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.databinding.FragmentLoginBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.LoginViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.module.LoginViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state.LoginUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.login.model.state.TestLoginUiState
import com.qubacy.geoqq.R
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(LoginViewModelModule::class)
@RunWith(AndroidJUnit4::class)
class LoginFragmentTest : BusinessFragmentTest<
    FragmentLoginBinding, LoginUiState, TestLoginUiState, LoginViewModel, LoginFragment
>() {
    override fun getFragmentClass(): Class<LoginFragment> {
        return LoginFragment::class.java
    }

    override fun getCurrentDestination(): Int {
        return R.id.loginFragment
    }

    @Test
    fun clickingSignInWithoutLoginDataLeadsToShowingErrorTest() {
        Espresso.onView(withId(R.id.fragment_login_button_login))
            .perform(ViewActions.click())
        Espresso.onView(withText())
            .perform(ViewActions.click())

    }

    @Test
    fun clickingSignInWithLoginLeadsToShowingErrorTest() {

    }

    @Test
    fun clickingSignInWithPasswordLeadsToShowingErrorTest() {

    }

    @Test
    fun clickingSignInWithFullLoginDataLeadsToSuccessfulSigningInTest() {

    }

    @Test
    fun clickingChangeLoginModeLeadsToShowingAndHidingRepeatPasswordTest() {

    }

    @Test
    fun clickingSignUpWithDifferentPasswordsLeadsToShowingErrorTest() {

    }

    @Test
    fun clickingSignUpWithFullSigningUpDataLeadsToSuccessfulSigningUpTest() {

    }
}