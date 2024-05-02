package com.qubacy.geoqq.data.myprofile.model.update

import android.net.Uri
import com.qubacy.geoqq.data._common.util.hasher.HasherUtil
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.myprofile.model._common.DataPrivacy
import com.qubacy.geoqq.data.myprofile.model._common.toMyProfilePrivacy
import com.qubacy.geoqq.data.myprofile.repository.source.http.api.request.MyProfileSecurityRequest
import com.qubacy.geoqq.data.myprofile.repository.source.http.api.request.UpdateMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.MyProfileDataStoreModel

data class DataMyProfileUpdateData(
    val aboutMe: String? = null,
    val avatarUri: Uri? = null,
    val security: DataSecurity? = null,
    val privacy: DataPrivacy? = null
) {

}

data class DataSecurity(
    val password: String,
    val newPassword: String
) {

}

@OptIn(ExperimentalStdlibApi::class)
fun DataSecurity.toMyProfileSecurityRequest(): MyProfileSecurityRequest {
    val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
    val newPasswordHashBytes = HasherUtil.hashString(newPassword, HasherUtil.HashAlgorithm.SHA256)

    val passwordHash = passwordHashBytes.toHexString()
    val newPasswordHash = newPasswordHashBytes.toHexString()

    return MyProfileSecurityRequest(passwordHash, newPasswordHash)
}

fun DataMyProfileUpdateData.toUpdateMyProfileRequest(
    avatarId: Long? = null
): UpdateMyProfileRequest {
    val security = security?.toMyProfileSecurityRequest()
    val privacy = privacy?.toMyProfilePrivacy()

    return UpdateMyProfileRequest(aboutMe, avatarId, security, privacy)
}

/**
 * Can be used for saving updated my profile data;
 */
fun DataMyProfileUpdateData.toMyProfileDataStoreModel(
    myProfile: MyProfileDataStoreModel,
    avatar: DataImage? = null
): MyProfileDataStoreModel {
    return myProfile.copy(
        avatarId = avatar?.id ?: myProfile.avatarId,
        aboutMe = aboutMe ?: myProfile.aboutMe,
        hitMeUpId = privacy?.hitMeUp?.id ?: myProfile.hitMeUpId
    )
}