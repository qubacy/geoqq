package com.qubacy.geoqq.ui.application.activity._common.screen._common.validator._common

interface Validator<T> {
    fun isValid(value: T): Boolean
}