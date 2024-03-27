package com.qubacy.geoqq.domain._common.model.user

import android.net.Uri

data class User(
    val id: Long,
    val username: String,
    val description: String?,
    val avatarUri: Uri,
    val isMate: Boolean,
    val isDeleted: Boolean
) {

}