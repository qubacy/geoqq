package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.validator.username

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.validator.Validator

class UsernameValidator : Validator<String> {
    companion object {
        val REGEX = Regex("^[А-Яа-яA-Za-z\\d_\\-\\s]{6,50}$")
    }

    override fun isValid(value: String): Boolean {
        return REGEX.matches(value)
    }
}