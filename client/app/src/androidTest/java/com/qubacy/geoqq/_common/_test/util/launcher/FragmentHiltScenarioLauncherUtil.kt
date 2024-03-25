package com.qubacy.geoqq._common._test.util.launcher

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.core.internal.deps.dagger.internal.Preconditions
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui.application.activity._common.HiltTestActivity

inline fun <T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = R.style.AppTheme,
    fragmentClass: Class<T>,
    navHostController: TestNavHostController,
    crossinline navHostControllerInitAction: TestNavHostController.() -> Unit,
    crossinline action: Fragment.() -> Unit = {}
): ActivityScenario<HiltTestActivity> {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(
        "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
        themeResId
    )

    return ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
        val rootView = activity.findViewById<View>(android.R.id.content)

        navHostController.setViewModelStore(activity.viewModelStore)

        navHostControllerInitAction(navHostController)
        Navigation.setViewNavController(rootView, navHostController)

        val fragmentClassRef = fragmentClass
        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(fragmentClassRef.classLoader) as ClassLoader,
            fragmentClassRef.name
        )
        fragment.arguments = fragmentArgs
        activity.supportFragmentManager
            .beginTransaction()
            .add(R.id.activity_main_fragment_container, fragment, "")
            .commitNow()

        fragment.action()
    }
}