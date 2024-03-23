package com.qubacy.geoqq.data.user.repository.source.http.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetUsersResponse(
    val users: List<GetUserResponse>
) {

}