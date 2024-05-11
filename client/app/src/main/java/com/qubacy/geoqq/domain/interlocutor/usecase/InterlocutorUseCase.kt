package com.qubacy.geoqq.domain.interlocutor.usecase

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.user.repository.impl.UserDataRepositoryImpl
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
    errorSource: LocalErrorDatabaseDataSourceImpl,
    private val mLogoutUseCase: LogoutUseCase,
    private val mUserDataRepository: UserDataRepositoryImpl
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    fun getInterlocutor(interlocutorId: Long) {
        executeLogic({
            val getUsersResultLiveData = mUserDataRepository.getUsersByIds(listOf(interlocutorId))

            var version = 0

            val initGetUsersResult = getUsersResultLiveData.awaitUntilVersion(version)
            val initInterlocutor = initGetUsersResult.users.first().toUser()

            mResultFlow.emit(GetInterlocutorDomainResult(interlocutor = initInterlocutor))

            if (initGetUsersResult.isNewest) return@executeLogic

            ++version

            val newestGetUsersResult = getUsersResultLiveData.awaitUntilVersion(version)
            val newestInterlocutor = newestGetUsersResult.users.first().toUser()

            mResultFlow.emit(UpdateInterlocutorDomainResult(interlocutor = newestInterlocutor))

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
            else -> throw IllegalArgumentException()
        }
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return mLogoutUseCase
    }
}