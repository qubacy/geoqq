package com.qubacy.geoqq.ui.screen.geochat.auth.common.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.ui.common.fragment.waiting.model.WaitingViewModel

abstract class AuthViewModel : WaitingViewModel() {
    protected var mAccessToken: MutableLiveData<String> = MutableLiveData()
    val accessToken: LiveData<String> = mAccessToken


}