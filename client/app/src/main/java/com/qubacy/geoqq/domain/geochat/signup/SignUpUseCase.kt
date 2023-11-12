package com.qubacy.geoqq.domain.geochat.signup

import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.signup.repository.SignUpDataRepository
import com.qubacy.geoqq.data.signup.repository.result.SignUpResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.geochat.signup.operation.ApproveSignUpOperation
import com.qubacy.geoqq.domain.geochat.signup.state.SignUpState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpUseCase(
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val signUpDataRepository: SignUpDataRepository
) : UseCase<SignUpState>(errorDataRepository) {
    fun signUp(login: String, password: String) {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = signUpDataRepository
            val result = signUpDataRepository.signUp(login, password)

            if (result is ErrorResult) return@launch processError(result.errorId)
            if (result is InterruptionResult) return@launch processInterruption()

            val resultCast = result as SignUpResult

            mCurrentRepository = tokenDataRepository
            val saveTokensResult = tokenDataRepository.saveTokens(
                resultCast.refreshToken, resultCast.accessToken)

            if (saveTokensResult is ErrorResult)
                return@launch processError(saveTokensResult.errorId)

            val operations = listOf(
                ApproveSignUpOperation()
            )

            mStateFlow.emit(SignUpState(operations))
        }
    }

    override fun generateState(operations: List<Operation>): SignUpState {
        return SignUpState(operations)
    }
}