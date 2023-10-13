package com.qubacy.geoqq.ui.screen.myprofile.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data.myprofile.MyProfileOperation
import com.qubacy.geoqq.ui.common.fragment.common.model.BaseViewModel
import com.qubacy.geoqq.ui.screen.myprofile.model.state.MyProfileUiState
import kotlinx.coroutines.flow.flowOf

// todo: provide a repository as a param..
class MyProfileViewModel : BaseViewModel() {
    private var mMyProfileOperationFlow = flowOf(MyProfileOperation())

    private val mMyProfileUiState = MutableLiveData<MyProfileUiState>()
    val myProfileUiState = mMyProfileUiState
}

class MyProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MyProfileViewModel::class.java))
            throw IllegalArgumentException()

        return MyProfileViewModel() as T
    }
}