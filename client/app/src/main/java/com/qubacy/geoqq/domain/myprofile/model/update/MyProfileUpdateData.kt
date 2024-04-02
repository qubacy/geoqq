package com.qubacy.geoqq.domain.myprofile.model.update

import android.net.Uri
import com.qubacy.geoqq.data.myprofile.model.update.DataMyProfileUpdateData
import com.qubacy.geoqq.data.myprofile.model.update.DataSecurity
import com.qubacy.geoqq.domain.myprofile.model._common.Privacy
import com.qubacy.geoqq.domain.myprofile.model._common.toDataPrivacy

data class MyProfileUpdateData(
    val aboutMe: String? = null,
    val avatarUri: Uri? = null,
    val security: Security? = null,
    val privacy: Privacy? = null
) {

}

data class Security(
    val password: String,
    val newPassword: String
) {

}

fun Security.toDataSecurity(): DataSecurity {
    return DataSecurity(password, newPassword)
}

fun MyProfileUpdateData.toDataMyProfileUpdateData(): DataMyProfileUpdateData {
    return DataMyProfileUpdateData(
        aboutMe, avatarUri, security?.toDataSecurity(), privacy?.toDataPrivacy())
}