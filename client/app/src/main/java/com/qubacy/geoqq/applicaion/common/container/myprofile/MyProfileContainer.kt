package com.qubacy.geoqq.applicaion.common.container.myprofile

import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModelFactory

abstract class MyProfileContainer() {
    abstract val myProfileViewModelFactory: MyProfileViewModelFactory
}