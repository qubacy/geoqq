package com.qubacy.geoqq.data.myprofile.entity.myprofile.validator.password

import com.qubacy.geoqq.data.myprofile.entity.myprofile.MyProfileEntityContext

class LoginPasswordValidator : PasswordValidator() {
    override fun getRegularExpression(): Regex {
        return Regex("^[\\S]{${MyProfileEntityContext.MIN_PASSWORD_LENGTH},${MyProfileEntityContext.MAX_PASSWORD_LENGTH}}\$")
    }
}