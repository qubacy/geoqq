package com.qubacy.geoqq.ui.screen.common.chat.model.state.operation

import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.data.common.entity.message.Message

class AddMessageUiOperation(
    val message: Message? = null,
    error: Error? = null
) : ChatUiOperation(error) {

}