package com.qubacy.geoqq.ui.screen.geochat.chat.model.state

import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.data.common.entity.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.ui.common.fragment.common.model.BaseUiState

class GeoChatUiState(
    val messages: MutableList<Message> = mutableListOf(),
    val users: MutableList<User> = mutableListOf(),
    error: Error? = null
) : BaseUiState(error) {

}