package com.qubacy.geoqq.domain.mate.chats.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.domain.mate.chats.model.MateChat

class ProcessDataMateChatsResult(
    val mateChats: List<MateChat>
) : Result() {

}