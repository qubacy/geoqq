package com.qubacy.geoqq.ui.common.component.bottomsheet.userinfo

import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.ui.common.component.bottomsheet.BottomSheetContentCallback

interface UserInfoBottomSheetContentCallback : BottomSheetContentCallback {
//    fun isUserFriend(user: User) // todo: think of this one..
    fun addToFriend(user: User)
}