package com.qubacy.geoqq.data.myprofile.model.avatar.linked

import android.net.Uri
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile

class DataMyProfileWithLinkedAvatar(
    username: String,
    description: String,
    hitUpOption: MyProfileDataModelContext.HitUpOption,
    val avatarUri: Uri
) : DataMyProfile(username, description, hitUpOption) {

}