package com.qubacy.geoqq._common.model.error.domain

enum class ErrorDomain(val id: Long) {
    DATABASE(0),
    NETWORK(1),
    TOKEN(2),
    IMAGE(3),
    LOCATION(4),
    UI_LOGIN(5),
    USER(6),
    UI_MY_PROFILE(7);

    companion object {
        const val DOMAIN_SIZE = 200
    }
}