package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import androidx.viewbinding.ViewBinding
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common._test.util.launcher.launchFragmentInHiltContainer
import com.qubacy.geoqq.ui.application.activity._common.HiltTestActivity
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.BaseFragment
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.Before
import org.junit.Rule
import org.junit.rules.RuleChain

abstract class BaseFragmentTest<
    ViewBindingType : ViewBinding,
    FragmentType : BaseFragment<ViewBindingType>
> {
    @get:Rule
    open val rule = RuleChain
        .outerRule(HiltAndroidRule(this))
        .around(GrantPermissionRule.grant(*getPermissionsToGrant()))

    protected lateinit var mActivityScenario: ActivityScenario<HiltTestActivity>
    protected lateinit var mFragment: FragmentType
    protected lateinit var mNavController: TestNavHostController

    protected var mNavArgs: Bundle? = null

    @Before
    open fun setup() {
        initDefaultNavArgs()
    }

    protected open fun initDefaultNavArgs() {

    }

    abstract fun getFragmentClass(): Class<FragmentType>
    @IdRes
    abstract fun getCurrentDestination(): Int

    @CallSuper
    open fun getPermissionsToGrant(): Array<String> {
        return arrayOf()
    }

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
        return mNavArgs
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
}