package com.qubacy.geoqq.domain.geochat.signin

import com.qubacy.geoqq.common.error.common.TypedErrorBase
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.common.repository.result.error.ErrorResult
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.signin.repository.result.SignInWithRefreshTokenResult
import com.qubacy.geoqq.data.signin.repository.result.SignInWithUsernamePasswordResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenValidityResult
import com.qubacy.geoqq.domain.geochat.signin.operation.ApproveSignInOperation
import com.qubacy.geoqq.domain.geochat.signin.state.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignInUseCase(
    val tokenDataRepository: TokenDataRepository,
    val signInDataRepository: SignInDataRepository
) {
    private val mSignInStateFlow = MutableStateFlow<SignInState?>(null)
    val signInStateFlow: StateFlow<SignInState?> = mSignInStateFlow

    private suspend fun processErrorResult(result: ErrorResult) {
        val state = createErrorState(result.error)

        mSignInStateFlow.emit(state)
    }

    suspend fun signInWithLocalToken() {
        val checkResult = tokenDataRepository.checkRefreshTokenValidity()

        if (checkResult is ErrorResult) {
            processErrorResult(checkResult)

            return
        }

        val signInResult = signInDataRepository
            .signInWithRefreshToken((checkResult as CheckRefreshTokenValidityResult).refreshToken)

        if (signInResult is ErrorResult) {
            processErrorResult(signInResult)

            return
        }

        val signInResultCast = signInResult as SignInWithRefreshTokenResult

        saveSignInTokens(signInResultCast.refreshToken, signInResultCast.accessToken)

        val operations = listOf(
            ApproveSignInOperation()
        )

        mSignInStateFlow.emit(SignInState(operations))
    }

    suspend fun signInWithUsernamePassword(
        login: String,
        password: String
    ) {
        val signInResult = signInDataRepository.signInWithUsernamePassword(login, password)

        if (signInResult is ErrorResult) {
            processErrorResult(signInResult)

            return
        }

        val signInResultCast = signInResult as SignInWithUsernamePasswordResult

        saveSignInTokens(signInResultCast.refreshToken, signInResultCast.accessToken)

        val operations = listOf(
            ApproveSignInOperation()
        )

        mSignInStateFlow.emit(SignInState(operations))
    }

    private suspend fun saveSignInTokens(
        refreshToken: String,
        accessToken: String
    ) {
        val tokensSaveResult = tokenDataRepository.saveTokens(refreshToken, accessToken)

        if (tokensSaveResult is ErrorResult) {
            processErrorResult(tokensSaveResult)

            return
        }
    }

    private fun createErrorState(error: TypedErrorBase): SignInState {
        val operations = listOf(
            HandleErrorOperation(error)
        )

        return SignInState(operations)
    }
}