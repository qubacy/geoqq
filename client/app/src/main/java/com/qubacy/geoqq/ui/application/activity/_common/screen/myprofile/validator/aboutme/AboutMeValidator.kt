package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.validator.aboutme

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.validator._common.Validator

class AboutMeValidator : Validator<String> {
    companion object {
        val REGEX = Regex("\\s*\\S+\\s*")
    }

    override fun isValid(value: String): Boolean {
        return REGEX.containsMatchIn(value)
    }
}