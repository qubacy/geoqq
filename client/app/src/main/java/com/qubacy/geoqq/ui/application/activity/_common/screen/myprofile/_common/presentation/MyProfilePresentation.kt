package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation

import android.net.Uri
import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.domain.myprofile.model.profile.MyProfile

data class MyProfilePresentation(
    val avatarUri: Uri,
    val login: String,
    val username: String,
    val aboutMe: String,
    val hitMeUp: HitMeUpType
) {

}

fun MyProfile.toMyProfilePresentation(): MyProfilePresentation {
    return MyProfilePresentation(avatar.uri, login, username, aboutMe, privacy.hitMeUp)
}