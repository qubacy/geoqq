package com.qubacy.geoqq.domain.geochat.signin

import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signin.repository.result.SignInWithLoginPasswordResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.domain.common.UseCase
import com.qubacy.geoqq.domain.geochat.signin.operation.ProcessSignInResultOperation
import com.qubacy.geoqq.domain.geochat.signin.state.SignInState

class SignInUseCase(
    val tokenDataRepository: TokenDataRepository,
    val signInDataRepository: SignInDataRepository,
    errorDataRepository: ErrorDataRepository
) : UseCase<SignInState>(errorDataRepository) {
    private suspend fun processSignInWithTokenResult(isSignedIn: Boolean) {
        val operations = listOf(
            ProcessSignInResultOperation(isSignedIn)
        )
        val state = generateState(operations)

        mStateFlow.emit(state)
    }

    suspend fun signInWithLocalToken() {
        mCurrentRepository = tokenDataRepository
        val signInResult = tokenDataRepository.getTokens()

        if (signInResult is ErrorResult) {
            if (signInResult.errorId == ErrorContext.Token.LOCAL_REFRESH_TOKEN_NOT_FOUND.id)
                return processSignInWithTokenResult(false)

            return processError(signInResult.errorId)
        }
        if (signInResult is InterruptionResult) return processInterruption()

        processSignInWithTokenResult(true)
    }

    suspend fun signInWithLoginPassword(
        login: String,
        password: String
    ) {
        mCurrentRepository = signInDataRepository
        val signInResult = signInDataRepository.signInWithLoginPassword(login, password)

        if (signInResult is ErrorResult) return processError(signInResult.errorId)
        if (signInResult is InterruptionResult) return processInterruption()

        val signInResultCast = signInResult as SignInWithLoginPasswordResult

        mCurrentRepository = tokenDataRepository
        val saveTokensResult = tokenDataRepository.saveTokens(
            signInResultCast.refreshToken, signInResultCast.accessToken)

        if (saveTokensResult is ErrorResult) return processError(saveTokensResult.errorId)
        if (saveTokensResult is InterruptionResult) return processInterruption()

        processSignInWithTokenResult(true)
    }

    override fun generateState(operations: List<Operation>): SignInState {
        return SignInState(operations)
    }
}