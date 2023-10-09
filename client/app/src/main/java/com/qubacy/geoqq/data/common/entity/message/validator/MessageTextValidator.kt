package com.qubacy.geoqq.data.common.entity.message.validator

import com.qubacy.geoqq.common.validator.Validator

class MessageTextValidator : Validator {
    override fun getRegularExpression(): Regex {
        return Regex("^.+$") // todo: should be improved;
    }
}