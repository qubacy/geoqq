package com.qubacy.geoqq.data._common.repository.message.util.extension

import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.repository.message.MessageDataRepository
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import com.qubacy.geoqq.data.user.repository.UserDataRepository

suspend fun MessageDataRepository.resolveGetMessagesResponse(
    userDataRepository: UserDataRepository,
    getMessagesResponse: GetMessagesResponse
): List<DataMessage> {
    val userIds = getMessagesResponse.messages.map { it.userId }.toSet().toList()
    val users = userDataRepository.resolveUsersWithLocalUser(userIds)

    return getMessagesResponse.messages.map { it.toDataMessage(users[it.userId]!!) }
}