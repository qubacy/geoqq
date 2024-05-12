package com.qubacy.geoqq.data.myprofile.repository._common._test.context

import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.data.image.repository._common._test.context.ImageDataRepositoryTestContext
import com.qubacy.geoqq.data.myprofile.model._common.DataPrivacy
import com.qubacy.geoqq.data.myprofile.model.profile.DataMyProfile
import com.qubacy.geoqq.data.myprofile.model.update.DataMyProfileUpdateData
import com.qubacy.geoqq.data.myprofile.model.update.DataSecurity
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api._common.MyProfilePrivacy
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.response.GetMyProfileResponse

object MyProfileDataRepositoryTestContext {
    val DEFAULT_AVATAR = ImageDataRepositoryTestContext.DEFAULT_DATA_IMAGE
    val DEFAULT_PASSWORD = "test"

    val DEFAULT_DATA_MY_PROFILE = DataMyProfile(
        "login", "test", "test",
        DEFAULT_AVATAR, DataPrivacy(HitMeUpType.EVERYBODY)
    )

    val DEFAULT_GET_MY_PROFILE_RESPONSE = GetMyProfileResponse(
        DEFAULT_DATA_MY_PROFILE.login,
        DEFAULT_DATA_MY_PROFILE.username,
        DEFAULT_DATA_MY_PROFILE.aboutMe,
        DEFAULT_DATA_MY_PROFILE.avatar.id,
        MyProfilePrivacy(DEFAULT_DATA_MY_PROFILE.privacy.hitMeUp.id)
    )

    val DEFAULT_DATA_MY_PROFILE_UPDATE_DATA = DataMyProfileUpdateData(
        DEFAULT_DATA_MY_PROFILE.username,
        DEFAULT_DATA_MY_PROFILE.aboutMe,
        DEFAULT_DATA_MY_PROFILE.avatar.uri,
        DataSecurity(DEFAULT_PASSWORD, DEFAULT_PASSWORD),
        DEFAULT_DATA_MY_PROFILE.privacy
    )
}