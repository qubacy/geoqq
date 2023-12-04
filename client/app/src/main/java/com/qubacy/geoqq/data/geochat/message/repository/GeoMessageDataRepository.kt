package com.qubacy.geoqq.data.geochat.message.repository

import com.qubacy.geoqq.data.common.repository.message.result.GetMessagesResult
import com.qubacy.geoqq.data.common.repository.message.source.network.model.response.MessageListResponse
import com.qubacy.geoqq.data.common.repository.message.source.network.model.response.common.toDataMessage
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.common.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.common.repository.network.updatable.UpdatableDataRepository
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.update.Update
import com.qubacy.geoqq.data.geochat.message.repository.result.SendGeoMessageResult
import com.qubacy.geoqq.data.geochat.message.repository.source.network.model.NetworkGeoMessageDataSource
import com.qubacy.geoqq.data.geochat.message.repository.source.websocket.WebSocketUpdateGeoMessageDataSource
import retrofit2.Call

class GeoMessageDataRepository(
    val networkGeoMessageDataSource: NetworkGeoMessageDataSource,
    val updateGeoMessageDataSource: WebSocketUpdateGeoMessageDataSource
) : UpdatableDataRepository(updateGeoMessageDataSource) {

    suspend fun getGeoMessages(
        radius: Int,
        latitude: Double,
        longitude: Double,
        accessToken: String
    ) {
        val getGeoMessagesNetworkCall = networkGeoMessageDataSource
            .getGeoMessages(radius, latitude, longitude, accessToken) as Call<Response>
        val getGeoMessagesNetworkCallResult = executeNetworkRequest(getGeoMessagesNetworkCall)

        if (getGeoMessagesNetworkCallResult is ErrorResult) return emitResult(getGeoMessagesNetworkCallResult)
        if (getGeoMessagesNetworkCallResult is InterruptionResult) return emitResult(getGeoMessagesNetworkCallResult)

        val getGeoMessagesNetworkResponse = (getGeoMessagesNetworkCallResult as ExecuteNetworkRequestResult)
            .response as MessageListResponse
        val messagesFromNetwork = getGeoMessagesNetworkResponse.messages.map{ it.toDataMessage() }

        emitResult(GetMessagesResult(messagesFromNetwork))

        val initUpdateSourceResult = initUpdateSource()

        if (initUpdateSourceResult is ErrorResult) return emitResult(initUpdateSourceResult)
    }

    suspend fun sendGeoMessage(
        radius: Int,
        latitude: Float,
        longitude: Float,
        accessToken: String
    ): Result {
        // todo: implement using WebSocketUpdateGeoMessageDataSource..



        return SendGeoMessageResult()
    }

    override fun processUpdates(updates: List<Update>) {
        TODO("Not yet implemented")
    }
}