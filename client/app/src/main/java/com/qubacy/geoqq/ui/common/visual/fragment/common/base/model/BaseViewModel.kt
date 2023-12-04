package com.qubacy.geoqq.ui.common.visual.fragment.common.base.model

import androidx.lifecycle.ViewModel

abstract class BaseViewModel() : ViewModel() {
    abstract fun retrieveError(errorId: Long)
}