package com.qubacy.geoqq.data._common.repository.message.util.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.repository.message.MessageDataRepository
import com.qubacy.geoqq.data._common.repository.message.result.ResolveMessagesDataResult
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

suspend fun MessageDataRepository.resolveGetMessagesResponse(
    userDataRepository: UserDataRepository,
    getMessagesResponse: GetMessagesResponse
): LiveData<ResolveMessagesDataResult> {
    val resultLiveData = MutableLiveData<ResolveMessagesDataResult>()

    val userIds = getMessagesResponse.messages.map { it.userId }.toSet().toList()
    val resolveUsersResultLiveData = userDataRepository.resolveUsersWithLocalUser(userIds)

    CoroutineScope(coroutineContext).launch {
        var version = 0

        while (true) {
            val resolveUsersResult = resolveUsersResultLiveData.awaitUntilVersion(version)
            val userIdUserMap = resolveUsersResult.userIdUserMap

            ++version

            val resolvedMessages = getMessagesResponse.messages.map {
                it.toDataMessage(userIdUserMap[it.userId]!!)
            }

            resultLiveData.postValue(ResolveMessagesDataResult(
                resolveUsersResult.isNewest, resolvedMessages))

            if (resolveUsersResult.isNewest) return@launch
        }
    }

    return resultLiveData
}