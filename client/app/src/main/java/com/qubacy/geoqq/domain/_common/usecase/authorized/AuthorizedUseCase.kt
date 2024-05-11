package com.qubacy.geoqq.domain._common.usecase.authorized

import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase

interface AuthorizedUseCase {
    fun getLogoutUseCase(): LogoutUseCase
}