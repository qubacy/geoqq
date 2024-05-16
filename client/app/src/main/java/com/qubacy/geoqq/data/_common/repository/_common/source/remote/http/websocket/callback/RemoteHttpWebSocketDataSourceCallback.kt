package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.callback

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.event._common.payload.EventPayload

interface RemoteHttpWebSocketDataSourceCallback {
    fun onMessageEventGotten(eventPayload: EventPayload)
    fun onErrorEventOccurred(error: Error)
    fun onClosedEventOccurred()
}