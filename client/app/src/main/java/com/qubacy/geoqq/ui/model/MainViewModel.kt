package com.qubacy.geoqq.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModel : ViewModel() {
    private var mAccessToken: String = ""
    val accessToken: String = mAccessToken

    fun setAccessToken(accessToken: String) {
        mAccessToken = accessToken

        // todo: saving the AccessToken using the DATA layer..
    }

    fun isAccessTokenCorrect(accessToken: String): Boolean {
        // todo: checking for validity using the DATA layer..

        return (accessToken.isNotEmpty())
    }
}

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MainViewModel::class.java))
            throw IllegalArgumentException()

        return MainViewModel() as T
    }
}