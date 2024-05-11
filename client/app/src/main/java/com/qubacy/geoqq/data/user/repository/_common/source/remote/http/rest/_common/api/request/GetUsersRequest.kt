package com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetUsersRequest(
    val ids: List<Long>
) {

}