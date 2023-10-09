package com.qubacy.geoqq.ui.screen.geochat.chat.adapter

import com.qubacy.geoqq.data.common.entity.person.user.User

interface GeoChatAdapterCallback {
    fun getUserById(userId: Long): User
}