package com.qubacy.geoqq.domain.geochat.signin

import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signin.repository.result.SignInWithLoginPasswordResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.domain.common.usecase.common.UseCase
import com.qubacy.geoqq.domain.geochat.signin.operation.ProcessSignInResultOperation
import com.qubacy.geoqq.domain.geochat.signin.state.SignInState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInUseCase(
    errorDataRepository: ErrorDataRepository,
    val tokenDataRepository: TokenDataRepository,
    val signInDataRepository: SignInDataRepository
) : UseCase<SignInState>(errorDataRepository) {
    private suspend fun processSignInWithTokenResult(isSignedIn: Boolean) {
        val operations = listOf(
            ProcessSignInResultOperation(isSignedIn)
        )
        val state = generateState(operations)

        mStateFlow.emit(state)
    }

    fun signInWithLocalToken() {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = tokenDataRepository
            val signInResult = tokenDataRepository.getTokens()

            if (signInResult is ErrorResult) {
                if (signInResult.errorId == ErrorContext.Token.LOCAL_REFRESH_TOKEN_NOT_FOUND.id)
                    return@launch processSignInWithTokenResult(false)

                return@launch processError(signInResult.errorId)
            }
            if (signInResult is InterruptionResult) return@launch processInterruption()

            processSignInWithTokenResult(true)
        }
    }

    fun signInWithLoginPassword(
        login: String,
        password: String
    ) {
        mCoroutineScope.launch(Dispatchers.IO) {
            mCurrentRepository = signInDataRepository
            val signInResult = signInDataRepository.signInWithLoginPassword(login, password)

            if (signInResult is ErrorResult) return@launch processError(signInResult.errorId)
            if (signInResult is InterruptionResult) return@launch processInterruption()

            val signInResultCast = signInResult as SignInWithLoginPasswordResult

            mCurrentRepository = tokenDataRepository
            val saveTokensResult = tokenDataRepository.saveTokens(
                signInResultCast.refreshToken, signInResultCast.accessToken
            )

            if (saveTokensResult is ErrorResult) return@launch processError(saveTokensResult.errorId)
            if (saveTokensResult is InterruptionResult) return@launch processInterruption()

            processSignInWithTokenResult(true)
        }
    }

    override fun generateState(operations: List<Operation>, prevState: SignInState?): SignInState {
        return SignInState(operations)
    }
}