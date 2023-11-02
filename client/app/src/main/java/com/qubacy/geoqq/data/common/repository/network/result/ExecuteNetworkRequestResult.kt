package com.qubacy.geoqq.data.common.repository.network.result

import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response

class ExecuteNetworkRequestResult(
    val response: retrofit2.Response<Response>
) : Result() {

}