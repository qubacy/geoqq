package com.qubacy.geoqq.domain.common.model

import android.net.Uri

class User(
    val id: Long,
    val username: String,
    val description: String,
    val avatarUri: Uri,
    val isMate: Boolean
) {

}