package com.qubacy.geoqq._common.model.error._common.domain

import com.qubacy.geoqq._common.model.error._common.domain.type.ErrorDomainType

enum class ErrorDomain(
    val type: ErrorDomainType
) {
    DATA_NETWORK(ErrorDomainType.LOCAL),
    DATA_TOKEN(ErrorDomainType.LOCAL),
    DATA_IMAGE_LOCAL(ErrorDomainType.LOCAL),
    UI_LOGIN(ErrorDomainType.LOCAL),
    UI_MY_PROFILE(ErrorDomainType.LOCAL),
    UI_CHAT(ErrorDomainType.LOCAL),
    UI_GEO(ErrorDomainType.LOCAL),
    DATA_NETWORK_WS(ErrorDomainType.LOCAL),
    DATA_NETWORK_REST(ErrorDomainType.LOCAL),

    GENERAL(ErrorDomainType.SHARED), // 1200
    AUTH(ErrorDomainType.SHARED), // 1400
    USER(ErrorDomainType.SHARED), // 1600
    MY_PROFILE(ErrorDomainType.SHARED), // 1800
    IMAGE(ErrorDomainType.SHARED), // 2000
    MATE(ErrorDomainType.SHARED), // 2200
    GEO(ErrorDomainType.SHARED); // 2400

    companion object {
        fun getOffsetForDomain(domain: ErrorDomain): Int {
            var offset = domain.type.offset
            val entriesOfType = entries.filter { it.type == domain.type }

            for (entry in entriesOfType) {
                if (entry == domain) break

                offset += entry.type.size
            }

            return offset
        }
    }
}