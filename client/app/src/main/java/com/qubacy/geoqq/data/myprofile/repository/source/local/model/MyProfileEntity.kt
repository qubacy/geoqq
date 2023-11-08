package com.qubacy.geoqq.data.myprofile.repository.source.local.model

import androidx.core.net.toUri
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext
import com.qubacy.geoqq.data.myprofile.model.avatar.linked.DataMyProfileWithLinkedAvatar
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile

data class MyProfileEntity(
    val avatarUri: String,
    val username: String,
    val description: String,
    val hitUpOptionIndex: Int
) {

}

fun MyProfileEntity.toDataMyProfile(): DataMyProfile {
    val hitUpOption = MyProfileDataModelContext.HitUpOption
        .entries.find {
            it.index == this.hitUpOptionIndex
        }!!
    val avatarUriCast = avatarUri.toUri()

    return DataMyProfileWithLinkedAvatar(username, description, hitUpOption, avatarUriCast)
}