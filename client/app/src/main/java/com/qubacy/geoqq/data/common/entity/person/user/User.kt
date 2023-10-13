package com.qubacy.geoqq.data.common.entity.person.user

import android.net.Uri
import com.qubacy.geoqq.data.common.entity.person.common.Person

class User(
    val userId: Long,
    username: String,
    description: String? = null,
    val isFriend: Boolean = false,
    avatarUri: Uri? = null,
) : Person(username, description, avatarUri) {

}