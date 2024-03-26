package com.qubacy.geoqq.data.mate.chat.repository.result

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat

class GetChatsDataResult(
    val chats: List<DataMateChat>
) : DataResult {

}