package com.qubacy.geoqq.domain.interlocutor.usecase

import android.util.Log
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.domain._common.model.user.toUser
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.GetInterlocutorDomainResult
import com.qubacy.geoqq.domain.interlocutor.usecase.result.interlocutor.UpdateInterlocutorDomainResult
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class InterlocutorUseCase @Inject constructor(
    errorSource: LocalErrorDataSource,
    private val mLogoutUseCase: LogoutUseCase,
    private val mUserDataRepository: UserDataRepository
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    fun getInterlocutor(interlocutorId: Long) {
        executeLogic({
            val getUsersResult = mUserDataRepository.getUsersByIds(listOf(interlocutorId))
                .await()
            val interlocutor = getUsersResult.users.first().toUser()

            mResultFlow.emit(GetInterlocutorDomainResult(interlocutor = interlocutor))

        }, {
            GetInterlocutorDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        mCoroutineScope.launch {
            mUserDataRepository.resultFlow.collect {
                processCollectedDataResult(it)
            }
        }
    }

    private suspend fun processCollectedDataResult(dataResult: DataResult) {
        when (dataResult::class) {
            GetUsersByIdsDataResult::class ->
                processGetUsersByIdsDataResult(dataResult as GetUsersByIdsDataResult)
            else -> throw IllegalArgumentException()
        }
    }

    private suspend fun processGetUsersByIdsDataResult(
        getUsersByIdsDataResult: GetUsersByIdsDataResult
    ) {
        val interlocutor = getUsersByIdsDataResult.users.first().toUser()

        Log.d(TAG, "processGetUsersByIdsDataResult(): interlocutor = $interlocutor;")

        mResultFlow.emit(UpdateInterlocutorDomainResult(interlocutor = interlocutor))
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return mLogoutUseCase
    }
}