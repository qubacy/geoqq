package com.qubacy.geoqq.data.mate.chat.repository._common.result

import com.qubacy.geoqq.data._common.repository.producing.result.ProducingDataResult
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat

class GetChatsDataResult(
    isNewest: Boolean,
    val offset: Int? = null,
    val chats: List<DataMateChat>? = null
) : ProducingDataResult(isNewest) {

}