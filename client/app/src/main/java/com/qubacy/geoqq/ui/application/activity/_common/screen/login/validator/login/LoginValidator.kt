package com.qubacy.geoqq.ui.application.activity._common.screen.login.validator.login

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.validator._common.Validator

class LoginValidator : Validator<String> {
    companion object {
        val REGEX = Regex("^\\S{8,32}$")
    }

    override fun isValid(value: String): Boolean {
        return REGEX.matches(value)
    }
}