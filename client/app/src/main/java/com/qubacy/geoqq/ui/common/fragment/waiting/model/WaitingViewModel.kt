package com.qubacy.geoqq.ui.common.fragment.waiting.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.ui.common.fragment.common.base.model.BaseViewModel

abstract class WaitingViewModel() : BaseViewModel() {
    protected val mIsWaiting = MutableLiveData<Boolean>(false)
    val isWaiting: LiveData<Boolean> = mIsWaiting
}