package com.qubacy.geoqq.applicaion.common.container.myprofile

import com.qubacy.geoqq.domain.myprofile.MyProfileUseCase
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModelFactory

class TestMyProfileContainer(
    private val myProfileUseCase: MyProfileUseCase
) : MyProfileContainer() {
    override val myProfileViewModelFactory = MyProfileViewModelFactory(myProfileUseCase)
}