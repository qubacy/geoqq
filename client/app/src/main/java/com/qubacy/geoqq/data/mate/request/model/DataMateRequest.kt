package com.qubacy.geoqq.data.mate.request.model

import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response.GetMateRequestResponse
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.websocket._common.event.payload.added.MateRequestAddedEventPayload
import com.qubacy.geoqq.data.user.model.DataUser

data class DataMateRequest(
    val id: Long,
    val user: DataUser
) {

}

fun GetMateRequestResponse.toDataMateRequest(user: DataUser): DataMateRequest {
    return DataMateRequest(id, user)
}

fun MateRequestAddedEventPayload.toDataMateRequest(user: DataUser): DataMateRequest {
    return DataMateRequest(id, user)
}