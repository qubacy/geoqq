package com.qubacy.geoqq.ui.common.fragment.model

import androidx.lifecycle.MutableLiveData

abstract class WaitingViewModel() : BaseViewModel() {
    protected val mIsWaiting = MutableLiveData<Boolean>(false)
    val isWaiting = mIsWaiting

}