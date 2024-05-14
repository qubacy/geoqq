package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.validator.message.text

import com.qubacy.geoqq.ui.application.activity._common.screen._common.validator._common.Validator

class MessageTextValidator : Validator<String> {
    companion object {
        val REGEX = Regex("^.{1,512}$")
    }

    override fun isValid(value: String): Boolean {
        return REGEX.matches(value)
    }
}