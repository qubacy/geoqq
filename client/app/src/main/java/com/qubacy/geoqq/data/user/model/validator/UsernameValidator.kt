package com.qubacy.geoqq.data.user.model.validator

import com.qubacy.geoqq.common.validator.Validator
import com.qubacy.geoqq.data.user.model.DataUser

class UsernameValidator() : Validator {
    override fun getRegularExpression(): Regex {
        return Regex("^[\\S]{${DataUser.MIN_USERNAME_LENGTH},${DataUser.MAX_USERNAME_LENGTH}}$")
    }
}