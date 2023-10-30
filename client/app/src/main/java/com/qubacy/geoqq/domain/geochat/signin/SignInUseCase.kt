package com.qubacy.geoqq.domain.geochat.signin

import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.repository.result.error.ErrorResult
import com.qubacy.geoqq.data.signin.repository.SignInDataRepository
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenExistenceResult
import com.qubacy.geoqq.domain.common.UseCase
import com.qubacy.geoqq.domain.geochat.signin.operation.ApproveSignInOperation
import com.qubacy.geoqq.domain.geochat.signin.operation.DeclineAutomaticSignInOperation
import com.qubacy.geoqq.domain.geochat.signin.state.SignInState

class SignInUseCase(
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
        val checkExistenceResult = signInDataRepository.tokenDataRepository
            .checkLocalRefreshTokenExistence()

        if (checkExistenceResult is ErrorResult) return processError(checkExistenceResult.error)

        val checkExistenceResultCast = checkExistenceResult as CheckRefreshTokenExistenceResult

        if (!checkExistenceResultCast.isExisting)
            return processLocalTokenDoesNotExist(checkExistenceResultCast)

        val checkResult = signInDataRepository.tokenDataRepository.checkRefreshTokenValidity()

        if (checkResult is ErrorResult) return processError(checkResult.error)

        val signInResult = signInDataRepository.signInWithRefreshToken()

        if (signInResult is ErrorResult) return processError(signInResult.error)

        val operations = listOf(
            ApproveSignInOperation()
        )

        if (mInterruptionFlag.get()) return mInterruptionFlag.set(false)

        mStateFlow.emit(SignInState(operations))
    }

    suspend fun signInWithUsernamePassword(
        login: String,
        password: String
    ) {
        val signInResult = signInDataRepository.signInWithUsernamePassword(login, password)

        if (signInResult is ErrorResult) return processError(signInResult.error)

        val operations = listOf(
            ApproveSignInOperation()
        )

        if (mInterruptionFlag.get()) return mInterruptionFlag.set(false)

        mStateFlow.emit(SignInState(operations))
    }

    override fun generateErrorState(operations: List<Operation>): SignInState {
        return SignInState(operations)
    }
}