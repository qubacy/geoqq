package com.qubacy.geoqq.data.mate.chat.repository.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.mate.chat.model.DataMateChat
import kotlinx.coroutines.flow.Flow

class GetChatsResult(
    val chatFlow: Flow<List<DataMateChat>>
) : Result() {

}