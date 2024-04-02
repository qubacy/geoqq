package com.qubacy.geoqq.data.myprofile.model

import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.myprofile.repository.source.http._common.MyProfilePrivacy
import com.qubacy.geoqq.data.myprofile.repository.source.http.request.MyProfileSecurityRequest
import com.qubacy.geoqq.data.myprofile.repository.source.http.request.UpdateMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository.source.http.response.GetMyProfileResponse
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.MyProfileDataStoreModel

data class DataMyProfile(
    val username: String,
    val aboutMe: String,
    val avatar: DataImage,
    val privacy: DataPrivacy
) {

}

data class DataPrivacy(
    val hitMeUp: HitMeUpType
) {

}

fun MyProfileDataStoreModel.toDataMyProfile(avatar: DataImage): DataMyProfile {
    val hitMeUpType = HitMeUpType.getHitMeUpTypeById(hitMeUpId)

    return DataMyProfile(username, aboutMe, avatar, DataPrivacy(hitMeUpType))
}

fun GetMyProfileResponse.toDataMyProfile(avatar: DataImage): DataMyProfile {
    return DataMyProfile(username, aboutMe, avatar, privacy.toDataPrivacy())
}

fun MyProfilePrivacy.toDataPrivacy(): DataPrivacy {
    val hitMeUpType = HitMeUpType.getHitMeUpTypeById(hitMeUpId)

    return DataPrivacy(hitMeUpType)
}

fun DataPrivacy.toMyProfilePrivacy(): MyProfilePrivacy {
    return MyProfilePrivacy(hitMeUp.id)
}

fun DataMyProfile.toMyProfileDataStoreModel(): MyProfileDataStoreModel {
    return MyProfileDataStoreModel(avatar.id, username, aboutMe, privacy.hitMeUp.id)
}

fun DataMyProfile.toUpdateMyProfileRequest(
    accessToken: String,
    password: String,
    newPassword: String
): UpdateMyProfileRequest {
    val security = MyProfileSecurityRequest(password, newPassword)
    val privacy = privacy.toMyProfilePrivacy()

    return UpdateMyProfileRequest(accessToken, aboutMe, avatar.id, security, privacy)
}