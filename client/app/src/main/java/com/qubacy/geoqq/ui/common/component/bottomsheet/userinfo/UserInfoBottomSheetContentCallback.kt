package com.qubacy.geoqq.ui.common.component.bottomsheet.userinfo

import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.ui.common.component.bottomsheet.BottomSheetContentCallback

interface UserInfoBottomSheetContentCallback : BottomSheetContentCallback {
//    fun isUserFriend(user: User) // todo: think of this one..
    fun addToMates(user: User)
}