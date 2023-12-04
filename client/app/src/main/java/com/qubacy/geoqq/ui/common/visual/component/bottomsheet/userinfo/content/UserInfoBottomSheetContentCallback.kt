package com.qubacy.geoqq.ui.common.visual.component.bottomsheet.userinfo.content

import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.ui.common.visual.component.bottomsheet.common.content.BottomSheetContentCallback

interface UserInfoBottomSheetContentCallback : BottomSheetContentCallback {
//    fun isUserFriend(user: User) // todo: think of this one..
    fun addToMates(user: User)
}