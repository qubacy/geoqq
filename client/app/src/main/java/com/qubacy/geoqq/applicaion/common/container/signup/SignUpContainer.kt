package com.qubacy.geoqq.applicaion.common.container.signup

import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModelFactory

abstract class SignUpContainer() {
    abstract val signUpViewModelFactory: SignUpViewModelFactory
}