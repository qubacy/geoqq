package com.qubacy.geoqq.data.mate.chat.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat

class GetChatsWithDatabaseResult(
    val chats: List<DataMateChat>
) : Result() {

}