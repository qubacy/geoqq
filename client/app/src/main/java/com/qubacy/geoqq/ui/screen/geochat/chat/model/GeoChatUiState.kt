package com.qubacy.geoqq.ui.screen.geochat.chat.model

import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.data.common.entity.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.ui.common.fragment.model.BaseUiState

class GeoChatUiState(
    val userList: List<User>,
    val messageList: List<Message>,
    error: Error? = null
) : BaseUiState(error) {

}