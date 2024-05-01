package com.qubacy.geoqq.ui.application.activity._common.screen.login.validator.login

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.validator._common.Validator

class LoginValidator : Validator<String> {
    companion object {
        val REGEX = Regex("^\\w{6,32}$")
    }

    override fun isValid(value: String): Boolean {
        return REGEX.matches(value)
    }
}