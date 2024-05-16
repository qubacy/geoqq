package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result.error

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.result._common.WebSocketResult

class WebSocketErrorResult(
    val error: Error
) : WebSocketResult {

}