package com.qubacy.geoqq.applicaion.common.container.signin

import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModelFactory

abstract class SignInContainer() {
    abstract val signInViewModelFactory: SignInViewModelFactory
}