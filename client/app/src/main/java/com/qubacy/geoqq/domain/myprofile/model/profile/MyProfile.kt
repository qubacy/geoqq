package com.qubacy.geoqq.domain.myprofile.model.profile

import com.qubacy.geoqq.data.myprofile.model.profile.DataMyProfile
import com.qubacy.geoqq.domain._common.model.image.Image
import com.qubacy.geoqq.domain._common.model.image.toImage
import com.qubacy.geoqq.domain.myprofile.model._common.Privacy
import com.qubacy.geoqq.domain.myprofile.model._common.toPrivacy

data class MyProfile(
    val login: String,
    val username: String,
    val aboutMe: String,
    val avatar: Image,
    val privacy: Privacy
) {

}

fun DataMyProfile.toMyProfile(): MyProfile {
    return MyProfile(login, username, aboutMe, avatar.toImage(), privacy.toPrivacy())
}