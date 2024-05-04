package com.qubacy.geoqq.data.mate.chat.repository.result

import com.qubacy.geoqq.data._common.repository.producing.result.ProducingDataResult
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat

class GetChatByIdDataResult(
    isNewest: Boolean,
    val chat: DataMateChat
) : ProducingDataResult(isNewest) {

}