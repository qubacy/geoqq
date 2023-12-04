package com.qubacy.geoqq.domain.common.model.common.validator.password.standard

import com.qubacy.geoqq.domain.common.model.common.validator.password.common.PasswordValidator
import com.qubacy.geoqq.domain.myprofile.model.MyProfileModelContext

class StandardPasswordValidator : PasswordValidator() {
    override fun getRegularExpression(): Regex {
        return Regex("^[\\S]{${MyProfileModelContext.MIN_PASSWORD_LENGTH},${MyProfileModelContext.MAX_PASSWORD_LENGTH}}\$")
    }
}