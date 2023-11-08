package com.qubacy.geoqq.data.myprofile.repository.source.network.model.response

import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext
import com.qubacy.geoqq.data.myprofile.model.avatar.labeled.DataMyProfileWithAvatarId
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.common.Privacy
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class GetMyProfileResponse(
    val username: String,
    @Json(name = "description") val description: String,
    @Json(name = "avatar-id") val avatarId: Long,
    val privacy: Privacy
) : Response() {

}

fun GetMyProfileResponse.toDataMyProfile(): DataMyProfile {
    val hitMeUpOption = MyProfileDataModelContext.HitUpOption.entries
        .find { it.index == privacy.hitMeUp }!!

    return DataMyProfileWithAvatarId(username, description, hitMeUpOption, avatarId)
}

