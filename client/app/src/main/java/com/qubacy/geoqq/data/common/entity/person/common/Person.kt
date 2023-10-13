package com.qubacy.geoqq.data.common.entity.person.common

import android.net.Uri

abstract class Person(
    val username: String,
    val description: String? = null,
    val avatarUri: Uri? = null,
) {

}