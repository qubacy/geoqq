package com.qubacy.geoqq.data.mate.message.repository

import com.qubacy.geoqq.data.common.repository.network.updatable.UpdatableDataRepository
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.update.Update
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.network.NetworkMateMessageDataSource
import com.qubacy.geoqq.data.mate.message.repository.source.websocket.WebSocketUpdateMateMessageDataSource

class MateMessageDataRepository(
    val localMateMessageDataSource: LocalMateMessageDataSource,
    val networkMateMessageDataSource: NetworkMateMessageDataSource,
    updateMateMessageDataSource: WebSocketUpdateMateMessageDataSource
) : UpdatableDataRepository(updateMateMessageDataSource) {

    override fun processUpdates(updates: List<Update>) {
        TODO("Not yet implemented")
    }
}