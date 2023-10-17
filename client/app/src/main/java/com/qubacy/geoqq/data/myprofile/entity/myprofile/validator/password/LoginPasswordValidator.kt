package com.qubacy.geoqq.data.myprofile.entity.myprofile.validator.password

import com.qubacy.geoqq.data.myprofile.entity.myprofile.MyProfileContext

class LoginPasswordValidator : PasswordValidator() {
    override fun getRegularExpression(): Regex {
        return Regex("^[\\S]{${MyProfileContext.MIN_PASSWORD_LENGTH},${MyProfileContext.MAX_PASSWORD_LENGTH}}\$")
    }
}