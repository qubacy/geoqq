package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.viewbinding.ViewBinding
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common._test.util.launcher.launchFragmentInHiltContainer
import com.qubacy.geoqq.ui.application.activity._common.HiltTestActivity
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.reflect.Field

abstract class BaseFragmentTest<
    ViewBindingType : ViewBinding,
    FragmentType : BaseFragment<ViewBindingType>
> {
    companion object {
        const val TEST_MESSAGE = "test message"
    }

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    protected lateinit var mActivityScenario: ActivityScenario<HiltTestActivity>
    protected lateinit var mFragment: FragmentType
    protected lateinit var mNavController: TestNavHostController

    @Before
    open fun setup() {

    }

    abstract fun getFragmentClass(): Class<FragmentType>
    @IdRes
    abstract fun getCurrentDestination(): Int

    /**
     * Meant to be called BEFORE any manipulations on mFragment;
     */
    protected fun init() {
        initMockedVars()
        initFragment()
    }

    protected open fun defaultInit() {
        init()
    }

    @CallSuper
    protected open fun initMockedVars() {
        mNavController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    protected open fun getFragmentArgs(): Bundle? {
        return null
    }

    private fun initFragment() {
        mActivityScenario = launchFragmentInHiltContainer(
            fragmentArgs = getFragmentArgs(),
            fragmentClass = getFragmentClass(),
            navHostController = mNavController,
            navHostControllerInitAction = {
                initNavController(this)
            }) {
                initFragmentOnActivity(this)
            }
    }

    private fun initNavController(navController: TestNavHostController) {
        navController.apply {
            setGraph(R.navigation.nav_graph)
            initNavControllerDestination(navController)
        }
    }

    protected open fun initNavControllerDestination(navController: TestNavHostController) {
        navController.setCurrentDestination(getCurrentDestination())
    }

    protected open fun initFragmentOnActivity(fragment: Fragment) {
        mFragment = fragment as FragmentType
    }

    protected fun getCurrentDestinationNavArgs(): Bundle? {
        return mNavController.backStack.last().arguments
    }

    @Test
    open fun showMessageTest() = runTest {
        defaultInit()

        val onPopupMessageOccurredMethodReflection = BaseFragment::class.java
            .getDeclaredMethod(
                "onPopupMessageOccurred", String::class.java, Int::class.java)
            .apply { isAccessible = true }

        mActivityScenario.onActivity {
            onPopupMessageOccurredMethodReflection.invoke(
                mFragment, TEST_MESSAGE, Toast.LENGTH_SHORT)
        }

        Espresso.onView(withText(TEST_MESSAGE))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}