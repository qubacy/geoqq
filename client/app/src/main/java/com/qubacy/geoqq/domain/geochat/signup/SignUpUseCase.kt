package com.qubacy.geoqq.domain.geochat.signup

import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.repository.result.error.ErrorResult
import com.qubacy.geoqq.data.signup.repository.SignUpDataRepository
import com.qubacy.geoqq.domain.common.UseCase
import com.qubacy.geoqq.domain.geochat.signup.operation.ApproveSignUpOperation
import com.qubacy.geoqq.domain.geochat.signup.state.SignUpState

class SignUpUseCase(
    val signUpDataRepository: SignUpDataRepository
) : UseCase<SignUpState>() {
    suspend fun signUp(login: String, password: String) {
        val result = signUpDataRepository.signUp(login, password)

        if (result is ErrorResult) return processError(result.error)

        val operations = listOf(
            ApproveSignUpOperation()
        )

        if (mInterruptionFlag.get()) return mInterruptionFlag.set(false)

        mStateFlow.emit(SignUpState(operations))
    }

    override fun generateErrorState(operations: List<Operation>): SignUpState {
        return SignUpState(operations)
    }
}