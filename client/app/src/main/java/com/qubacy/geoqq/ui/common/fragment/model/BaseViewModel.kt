package com.qubacy.geoqq.ui.common.fragment.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qubacy.geoqq.common.error.Error

abstract class BaseViewModel : ViewModel() {
    private val mError = MutableLiveData<Error>(null)
    val error: LiveData<Error> = mError
}