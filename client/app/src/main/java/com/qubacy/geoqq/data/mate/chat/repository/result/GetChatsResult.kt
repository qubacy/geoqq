package com.qubacy.geoqq.data.mate.chat.repository.result

import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.data.common.repository.common.result.common.Result

class GetChatsResult(
    val chats: List<Chat>
) : Result() {

}