package com.qubacy.geoqq.common.validator

interface Validator {
    fun check(string: String): Boolean {
        return getRegularExpression().matches(string)
    }
    fun getRegularExpression(): Regex
}