package com.qubacy.geoqq.data.user.repository.source.http.api.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetUsersRequest(
    val ids: List<Long>
) {

}