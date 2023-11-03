package com.qubacy.geoqq.common.error.common

data class Error(
    val id: Long,
    val message: String,
    val isCritical: Boolean
) {

}