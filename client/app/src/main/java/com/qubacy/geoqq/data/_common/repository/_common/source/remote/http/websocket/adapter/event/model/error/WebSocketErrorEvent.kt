package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model.error

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.adapter.event.model._common.WebSocketEvent

// todo: think of the structure..
class WebSocketErrorEvent(
    val error: Error
) : WebSocketEvent {

}