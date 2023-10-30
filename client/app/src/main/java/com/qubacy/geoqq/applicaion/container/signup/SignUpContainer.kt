package com.qubacy.geoqq.applicaion.container.signup

import com.qubacy.geoqq.domain.geochat.signup.SignUpUseCase
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModelFactory

class SignUpContainer(
    private val mSingUpUseCase: SignUpUseCase
) {
    val signUpViewModelFactory = SignUpViewModelFactory(mSingUpUseCase)
}