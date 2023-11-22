package com.qubacy.geoqq.applicaion.common.container.signin

import com.qubacy.geoqq.domain.geochat.signin.SignInUseCase
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModelFactory

class SignInContainerImpl(
    private val mSignInUseCase: SignInUseCase
) : SignInContainer() {
    override val signInViewModelFactory = SignInViewModelFactory(mSignInUseCase)
}