package com.qubacy.geoqq.data.common.entity.person.common.validator.username

import com.qubacy.geoqq.common.validator.Validator
import com.qubacy.geoqq.data.common.entity.person.common.PersonContext

class UsernameValidator() : Validator {
    override fun getRegularExpression(): Regex {
        return Regex("^[\\S]{${PersonContext.MIN_USERNAME_LENGTH},${PersonContext.MAX_USERNAME_LENGTH}}$")
    }
}