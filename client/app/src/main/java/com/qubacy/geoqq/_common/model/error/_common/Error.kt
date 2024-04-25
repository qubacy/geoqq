package com.qubacy.geoqq._common.model.error._common

data class Error(
    val id: Long,
    val message: String,
    val isCritical: Boolean
) {

}