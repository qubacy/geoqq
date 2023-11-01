package com.qubacy.geoqq.domain.geochat.signin

import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenExistenceResult
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenValidityResult
import com.qubacy.geoqq.domain.common.UseCase
import com.qubacy.geoqq.domain.common.error.ErrorEnum
import com.qubacy.geoqq.domain.geochat.signin.operation.ApproveSignInOperation
import com.qubacy.geoqq.domain.geochat.signin.operation.DeclineAutomaticSignInOperation
import com.qubacy.geoqq.domain.geochat.signin.state.SignInState

class SignInUseCase(
    val tokenDataRepository: TokenDataRepository,
    val signInDataRepository: SignInDataRepository
) : UseCase<SignInState>() {
    private suspend fun processLocalTokenDoesNotExist(
        result: CheckRefreshTokenExistenceResult
    ) {
        val operations = listOf(
            DeclineAutomaticSignInOperation()
        )

        val state = SignInState(operations)

        mStateFlow.emit(state)
    }

    suspend fun signInWithLocalToken() {
        mCurrentRepository = tokenDataRepository
        val checkExistenceResult = tokenDataRepository.checkLocalRefreshTokenExistence()

        if (checkExistenceResult is ErrorResult) return processError(checkExistenceResult.error)
        if (checkExistenceResult is InterruptionResult) return mInterruptionFlag.set(false)

        val checkExistenceResultCast = checkExistenceResult as CheckRefreshTokenExistenceResult

        if (!checkExistenceResultCast.isExisting)
            return processLocalTokenDoesNotExist(checkExistenceResultCast)

        mCurrentRepository = tokenDataRepository
        val checkResult = tokenDataRepository.checkRefreshTokenValidity()

        if (checkResult is ErrorResult) return processError(checkResult.error)
        if (checkResult is InterruptionResult) return mInterruptionFlag.set(false)

        val checkResultCast = checkResult as CheckRefreshTokenValidityResult

        if (!checkResultCast.isValid) return processError(ErrorEnum.INVALID_TOKEN.error)

        mCurrentRepository = tokenDataRepository
        val signInResult = tokenDataRepository.updateTokens()

        if (signInResult is ErrorResult) return processError(signInResult.error)
        if (signInResult is InterruptionResult) return mInterruptionFlag.set(false)

        val operations = listOf(
            ApproveSignInOperation()
        )

        mStateFlow.emit(SignInState(operations))
    }

    suspend fun signInWithLoginPassword(
        login: String,
        password: String
    ) {
        mCurrentRepository = signInDataRepository
        val signInResult = signInDataRepository.signInWithLoginPassword(login, password)

        if (signInResult is ErrorResult) return processError(signInResult.error)
        if (signInResult is InterruptionResult) return mInterruptionFlag.set(false)

        val operations = listOf(
            ApproveSignInOperation()
        )

        mStateFlow.emit(SignInState(operations))
    }

    override fun generateErrorState(operations: List<Operation>): SignInState {
        return SignInState(operations)
    }
}