package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.validator.password

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.validator.Validator

class PasswordValidator : Validator<String> {
    companion object {
        val REGEX = Regex("^\\S{8,32}$")
    }

    override fun isValid(value: String): Boolean {
        return REGEX.matches(value)
    }
}