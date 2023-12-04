package com.qubacy.geoqq.data.myprofile.model.avatar.linked

import android.net.Uri
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile

class DataMyProfileWithLinkedAvatar(
    username: String,
    description: String,
    hitUpOption: DataMyProfile.HitUpOption,
    val avatarUri: Uri
) : DataMyProfile(username, description, hitUpOption) {

}