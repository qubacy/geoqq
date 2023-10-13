package com.qubacy.geoqq.ui.screen.myprofile.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.common.fragment.common.model.BaseViewModel

class MyProfileViewModel : BaseViewModel() {

}

class MyProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MyProfileViewModel::class.java))
            throw IllegalArgumentException()

        return MyProfileViewModel() as T
    }
}