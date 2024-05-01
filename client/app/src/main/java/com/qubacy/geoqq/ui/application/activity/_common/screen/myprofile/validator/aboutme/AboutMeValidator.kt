package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.validator.aboutme

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base._common.validator._common.Validator

class AboutMeValidator : Validator<String> {
    companion object {
        val REGEX = Regex("^.{0,2048}$")
    }

    override fun isValid(value: String): Boolean {
        return REGEX.containsMatchIn(value)
    }
}