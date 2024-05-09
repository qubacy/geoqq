package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized

import androidx.navigation.NavController
import androidx.test.core.app.ActivityScenario
import com.qubacy.geoqq.R
import org.junit.Assert
import org.junit.Test

interface AuthorizationFragmentTest {
    @Test
    fun onAuthorizedFragmentLogoutTest() {
        navigateToLoginTest()
    }

    @Test
    fun navigateToLoginTest() {
        beforeNavigateToLoginTest()

        val navController = getAuthorizationFragmentNavController()
        val loginAction = getAuthorizationFragmentLoginAction()

        val expectedDestinationId = R.id.loginFragment

        getAuthorizationFragmentActivityScenario().onActivity {
            navController.navigate(loginAction)
        }

        val gottenDestinationId = navController.currentDestination!!.id

        Assert.assertEquals(expectedDestinationId, gottenDestinationId)
    }

    fun beforeNavigateToLoginTest()

    fun getAuthorizationFragmentNavController(): NavController

    fun getAuthorizationFragmentActivityScenario(): ActivityScenario<*>

    fun getAuthorizationFragmentLoginAction(): Int
}