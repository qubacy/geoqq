package com.qubacy.geoqq.data.myprofile.model.avatar.labeled

import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile

class DataMyProfileWithAvatarId(
    username: String,
    description: String,
    hitUpOption: DataMyProfile.HitUpOption,
    val avatarId: Long
) : DataMyProfile(username, description, hitUpOption) {

}