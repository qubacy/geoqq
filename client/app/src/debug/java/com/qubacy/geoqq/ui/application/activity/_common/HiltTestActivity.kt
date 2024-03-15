package com.qubacy.geoqq.ui.application.activity._common

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HiltTestActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (findViewById<View>(android.R.id.content) as ViewGroup).removeAllViews()
    }
}