package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.extension

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.qubacy.geoqq.ui.application.activity._common.MainActivity
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.BaseFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunner
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.util.permission.PermissionRunnerCallback

fun BaseFragment<*>.closeSoftKeyboard() {
    val inputMethodManager =
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

    inputMethodManager?.hideSoftInputFromWindow(requireView().windowToken, 0)
}

fun <FragmentType>BaseFragment<*>.runPermissionCheck(

) where FragmentType : Fragment, FragmentType : PermissionRunnerCallback {
   PermissionRunner<FragmentType>(this as FragmentType).requestPermissions()
}

fun <T>BaseFragment<*>.getNavigationResult(key: String = "result"): MutableLiveData<T?>? {
    return Navigation.findNavController(requireView())
        .currentBackStackEntry?.savedStateHandle?.getLiveData(key)
}

fun <T>BaseFragment<*>.setNavigationResult(result: T, key: String = "result") {
    Navigation.findNavController(requireView())
        .previousBackStackEntry?.savedStateHandle?.set(key, result)
}

/**
 * Should be called in order to bind a screen's Toolbar and the Main Activity's NavigationView.
 */
fun BaseFragment<*>.setupNavigationUI(toolbar: MaterialToolbar) {
    val mainActivity = requireActivity() as MainActivity
    val navController = Navigation.findNavController(requireView())

    val appBarConfiguration = AppBarConfiguration(
        mainActivity.topDestinations, mainActivity.navigationDrawerLayout)

    toolbar.setupWithNavController(navController, appBarConfiguration)

    mainActivity.navigationDrawer.apply {
        setupWithNavController(navController)
        setHeaderTitle(toolbar.title.toString())
    }
}