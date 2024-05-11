package com.qubacy.geoqq.data.user.repository._common

import androidx.lifecycle.LiveData
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.user.model.DataUser
import com.qubacy.geoqq.data.user.repository._common.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.data.user.repository._common.result.ResolveUsersDataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class UserDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    abstract suspend fun getUsersByIds(userIds: List<Long>): LiveData<GetUsersByIdsDataResult>
    abstract suspend fun resolveUsers(userIds: List<Long>): LiveData<ResolveUsersDataResult>
    open suspend fun resolveLocalUser(): DataUser {
        val localUserId = getLocalUserId()

        return getUsersByIds(listOf(localUserId)).await().users.first()
    }
    open suspend fun resolveUsersWithLocalUser(
        userIds: List<Long>
    ): LiveData<ResolveUsersDataResult> {
        val localUserId = getLocalUserId()

        return if (userIds.contains(localUserId)) resolveUsers(userIds)
        else resolveUsers(userIds.plus(localUserId))
    }
    abstract fun getLocalUserId(): Long
}