package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common._test.context

import android.net.Uri
import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile._common.presentation.MyProfilePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model._common.state.input.MyProfileInputData

object MyProfileTestContext {
    fun generateMyProfilePresentation(
        avatar: ImagePresentation
    ): MyProfilePresentation {
        return MyProfilePresentation(
            avatar.uri,
            "test",
            "test",
            "test about",
            HitMeUpType.EVERYBODY
        )
    }

    fun generateMyProfileInputData(
        avatarUri: Uri? = null,
        username: String = "test",
        aboutMe: String = "test about",
        password: String = String(),
        newPassword: String = String(),
        newPasswordAgain: String = String(),
        hitMeUpType: HitMeUpType = HitMeUpType.EVERYBODY
    ): MyProfileInputData {
        return MyProfileInputData(
            avatarUri,
            username, aboutMe,
            password, newPassword, newPasswordAgain,
            hitMeUpType
        )
    }
}