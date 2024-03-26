package com.qubacy.geoqq.data.user.repository.source.http.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetUserResponse(
    @Json(name = ID_PROP_NAME) val id: Long,
    @Json(name = USERNAME_PROP_NAME) val username: String,
    @Json(name = DESCRIPTION_PROP_NAME) val description: String,
    @Json(name = AVATAR_ID_PROP_NAME) val avatarId: Long,
    @Json(name = IS_MATE_PROP_NAME) val isMate: Boolean,
    @Json(name = IS_DELETED_PROP_NAME) val isDeleted: Boolean
) {
    companion object {
        const val ID_PROP_NAME = "id"
        const val USERNAME_PROP_NAME = "username"
        const val DESCRIPTION_PROP_NAME = "description"
        const val AVATAR_ID_PROP_NAME = "avatar-id"
        const val IS_MATE_PROP_NAME = "is-mate"
        const val IS_DELETED_PROP_NAME = "is-deleted"
    }
}