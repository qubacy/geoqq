package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common.validator

interface Validator<T> {
    fun isValid(value: T): Boolean
}