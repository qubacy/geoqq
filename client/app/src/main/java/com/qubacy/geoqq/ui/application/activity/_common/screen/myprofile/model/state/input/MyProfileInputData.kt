package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.state.input

import android.net.Uri
import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.domain.myprofile.model._common.Privacy
import com.qubacy.geoqq.domain.myprofile.model.update.MyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.model.update.Security
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.MyProfilePresentation

data class MyProfileInputData(
    val avatarUri: Uri? = null,
    val aboutMe: String? = null,
    val password: String? = null,
    val newPassword: String? = null,
    val newPasswordAgain: String? = null,
    val hitMeUp: HitMeUpType? = null
) {
    fun isEmpty(): Boolean {
        return (
            avatarUri == null && aboutMe == null &&
            password == null && newPassword == null &&
            newPasswordAgain == null && hitMeUp == null
        )
    }
}

fun MyProfileInputData.toMyProfileUpdateData(): MyProfileUpdateData {
    val security = if (password != null && newPassword != null)
        Security(password, newPassword) else null
    val privacy = if (hitMeUp != null) Privacy(hitMeUp) else null

    return MyProfileUpdateData(aboutMe, avatarUri, security, privacy)
}

fun MyProfileInputData.toUpdatedMyProfilePresentation(
    prevMyProfilePresentation: MyProfilePresentation
): MyProfilePresentation {
    return prevMyProfilePresentation.copy(
        avatarUri = avatarUri ?: prevMyProfilePresentation.avatarUri,
        aboutMe = aboutMe ?: prevMyProfilePresentation.aboutMe,
        hitMeUp = hitMeUp ?: prevMyProfilePresentation.hitMeUp
    )
}