package com.qubacy.geoqq._common.error.domain

enum class ErrorDomain(val id: Long) {
    DATABASE(0),
    NETWORK(1),
    TOKEN(2),
    IMAGE(3),
    LOCATION(4),
    UI(5),
    USER(6);

    companion object {
        const val DOMAIN_SIZE = 200
    }
}