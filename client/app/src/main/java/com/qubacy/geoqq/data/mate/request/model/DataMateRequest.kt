package com.qubacy.geoqq.data.mate.request.model

import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestResponse
import com.qubacy.geoqq.data.user.model.DataUser

data class DataMateRequest(
    val id: Long,
    val user: DataUser
) {

}

fun GetMateRequestResponse.toDataMateRequest(user: DataUser): DataMateRequest {
    return DataMateRequest(id, user)
}