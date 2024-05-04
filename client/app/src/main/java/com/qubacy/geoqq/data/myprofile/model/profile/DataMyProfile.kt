package com.qubacy.geoqq.data.myprofile.model.profile

import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.myprofile.model._common.DataPrivacy
import com.qubacy.geoqq.data.myprofile.model._common.toDataPrivacy
import com.qubacy.geoqq.data.myprofile.repository.source.http.api.response.GetMyProfileResponse
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.MyProfileDataStoreModel

data class DataMyProfile(
    val login: String,
    val username: String,
    val aboutMe: String,
    val avatar: DataImage,
    val privacy: DataPrivacy
) {

}

fun MyProfileDataStoreModel.toDataMyProfile(avatar: DataImage): DataMyProfile {
    val hitMeUpType = HitMeUpType.getHitMeUpTypeById(hitMeUpId)

    return DataMyProfile(login, username, aboutMe, avatar, DataPrivacy(hitMeUpType))
}

fun GetMyProfileResponse.toDataMyProfile(avatar: DataImage): DataMyProfile {
    return DataMyProfile(login, username, aboutMe, avatar, privacy.toDataPrivacy())
}

fun DataMyProfile.toMyProfileDataStoreModel(): MyProfileDataStoreModel {
    return MyProfileDataStoreModel(avatar.id, login, username, aboutMe, privacy.hitMeUp.id)
}