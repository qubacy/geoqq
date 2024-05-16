package com.qubacy.geoqq.data.user.repository._common.source.remote.http.websocket._common

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.RemoteHttpWebSocketDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.callback.RemoteHttpWebSocketDataSourceCallback

abstract class RemoteUserHttpWebSocketDataSource(
    callback: RemoteHttpWebSocketDataSourceCallback
) : RemoteHttpWebSocketDataSource(callback) {

}