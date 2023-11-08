package com.qubacy.geoqq.domain.myprofile.model

object MyProfileModelContext {
    const val MAX_AVATAR_SIZE = 1000

    const val MIN_PASSWORD_LENGTH = 8
    const val MAX_PASSWORD_LENGTH = 32

    const val USER_AVATAR_URI_KEY = "userAvatarProperty"
    const val DESCRIPTION_TEXT_KEY = "descriptionProperty"

    const val CURRENT_PASSWORD_TEXT_KEY = "currentPasswordProperty"
    const val NEW_PASSWORD_TEXT_KEY = "newPasswordProperty"
    const val REPEAT_NEW_PASSWORD_TEXT_KEY = "repeatNewPasswordProperty"

    const val PRIVACY_HIT_UP_POSITION_KEY = "privacyHitUpProperty"
}