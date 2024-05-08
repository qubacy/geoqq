package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.validator.username

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.validator._common.Validator

class UsernameValidator : Validator<String> {
    companion object {
        val REGEX = Regex("^.{6,128}$")
    }

    override fun isValid(value: String): Boolean {
        return REGEX.matches(value)
    }
}