package com.qubacy.geoqq.applicaion.container.myprofile

import com.qubacy.geoqq.domain.myprofile.MyProfileUseCase
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModelFactory

class MyProfileContainer(
    private val myProfileUseCase: MyProfileUseCase
) {
    val myProfileViewModel = MyProfileViewModelFactory(myProfileUseCase)
}