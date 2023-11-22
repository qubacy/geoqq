package com.qubacy.geoqq.applicaion.common.container.myprofile

import com.qubacy.geoqq.domain.myprofile.MyProfileUseCase
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModelFactory

class MyProfileContainer(
    private val myProfileUseCase: MyProfileUseCase
) {
    val myProfileViewModelFactory = MyProfileViewModelFactory(myProfileUseCase)
}