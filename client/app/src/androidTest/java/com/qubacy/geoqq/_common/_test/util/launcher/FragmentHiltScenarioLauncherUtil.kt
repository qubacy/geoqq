package com.qubacy.geoqq._common._test.util.launcher

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
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
        navHostController.setViewModelStore(activity.viewModelStore)

        navHostControllerInitAction(navHostController)

        val fragmentClassRef = fragmentClass
        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(fragmentClassRef.classLoader) as ClassLoader,
            fragmentClassRef.name
        )

        fragment.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event.targetState != Lifecycle.State.STARTED) return

                Log.d("TEST", "onStateChanged(): view.tag = " +
                        "${fragment.requireView()
                            .getTag(androidx.navigation.R.id.nav_controller_view_tag)}")

                Navigation.setViewNavController(fragment.requireView(), navHostController)
            }
        })

        fragment.arguments = fragmentArgs
        activity.supportFragmentManager
            .beginTransaction()
            .add(R.id.activity_main_fragment_container, fragment, "")
//            .runOnCommit {
//                fragment.lifecycle.addObserver(object : LifecycleEventObserver {
//                    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
//                        if (event.targetState != Lifecycle.State.STARTED) return
//
//                        Navigation.setViewNavController(fragment.requireView(), navHostController)
//                    }
//                })
//            }
            .commitNow()

        fragment.action()
    }
}