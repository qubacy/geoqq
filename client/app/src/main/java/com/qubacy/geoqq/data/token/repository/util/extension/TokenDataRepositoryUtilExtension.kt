package com.qubacy.geoqq.data.token.repository.util.extension

import android.util.Log
import com.qubacy.geoqq.data._common.util.base64.Base64Util
import com.qubacy.geoqq.data._common.util.hasher.HasherUtil
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.token.repository.TokenDataRepository

suspend fun TokenDataRepository.signIn(
    login: String,
    password: String
) {
    val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
    val passwordHash = Base64Util.bytesToString(passwordHashBytes)

    Log.d(TokenDataRepository.TAG, "signIn(): passwordHash = $passwordHash;")

    val signInRequest = httpTokenDataSource.signIn(login, passwordHash)
    val signInResponse = executeNetworkRequest(errorDataRepository, signInRequest)

    localTokenDataSource.saveTokens(
        signInResponse.accessToken,
        signInResponse.refreshToken
    )
}

suspend fun TokenDataRepository.signUp(
    login: String,
    password: String
) {
    val passwordHashBytes = HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)
    val passwordHash = Base64Util.bytesToString(passwordHashBytes)

    val signUpRequest = httpTokenDataSource.signUp(login, passwordHash)
    val signUpResponse = executeNetworkRequest(errorDataRepository, signUpRequest)

    localTokenDataSource.saveTokens(
        signUpResponse.accessToken,
        signUpResponse.refreshToken
    )
}