package com.qubacy.geoqq.ui.application.activity._common

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HiltTestActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager.fragments[0] as NavHostFragment
        val startFragment = navHostFragment.childFragmentManager.fragments[0]

        navHostFragment.childFragmentManager
            .beginTransaction()
            .remove(startFragment)
            .commitNow()
    }
}