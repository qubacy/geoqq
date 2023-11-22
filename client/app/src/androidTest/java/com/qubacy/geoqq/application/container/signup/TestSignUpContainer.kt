package com.qubacy.geoqq.applicaion.common.container.signup

import com.qubacy.geoqq.domain.geochat.signup.SignUpUseCase
import com.qubacy.geoqq.ui.screen.geochat.auth.signup.model.SignUpViewModelFactory

class TestSignUpContainer(
    mSingUpUseCase: SignUpUseCase
) : SignUpContainer() {
    override val signUpViewModelFactory = SignUpViewModelFactory(mSingUpUseCase)
}