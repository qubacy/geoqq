package com.qubacy.geoqq.applicaion.common.container.signin

import com.qubacy.geoqq.domain.geochat.signin.SignInUseCase
import com.qubacy.geoqq.ui.screen.geochat.auth.signin.model.SignInViewModelFactory

class SignInContainer(
    private val mSignInUseCase: SignInUseCase
) {
    val signInViewModelFactory = SignInViewModelFactory(mSignInUseCase)

}