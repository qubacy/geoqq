package com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common

import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignInResponse
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignUpResponse

interface RemoteAuthHttpRestDataSource {
    fun signIn(login: String, passwordHash: String): SignInResponse
    fun signUp(login: String, passwordHash: String): SignUpResponse
}