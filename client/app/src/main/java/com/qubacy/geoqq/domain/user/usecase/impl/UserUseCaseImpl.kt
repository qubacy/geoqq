package com.qubacy.geoqq.domain.user.usecase.impl

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import com.qubacy.geoqq.domain._common.model.user.toUser
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain.user.usecase._common.UserUseCase
import com.qubacy.geoqq.domain.user.usecase._common.result.get.GetUserDomainResult
import com.qubacy.geoqq.domain.user.usecase._common.result.update.UpdateUserDomainResult
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import javax.inject.Inject

class UserUseCaseImpl @Inject constructor(
    errorSource: LocalErrorDatabaseDataSource,
    private val mLogoutUseCase: LogoutUseCase,
    private val mUserDataRepository: UserDataRepository
) : UserUseCase(errorSource) {
    override fun getUser(userId: Long) {
        executeLogic({
            val getUsersResultLiveData = mUserDataRepository.getUsersByIds(listOf(userId))

            var version = 0

            val initGetUsersResult = getUsersResultLiveData.awaitUntilVersion(version)
            val initInterlocutor = initGetUsersResult.users.first().toUser()

            mResultFlow.emit(GetUserDomainResult(interlocutor = initInterlocutor))

            if (initGetUsersResult.isNewest) return@executeLogic

            ++version

            val newestGetUsersResult = getUsersResultLiveData.awaitUntilVersion(version)
            val newestInterlocutor = newestGetUsersResult.users.first().toUser()

            mResultFlow.emit(UpdateUserDomainResult(interlocutor = newestInterlocutor))

        }, {
            GetUserDomainResult(error = it)
        }, ::authorizedErrorMiddleware)
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return mLogoutUseCase
    }
}