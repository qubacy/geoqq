package com.qubacy.geoqq.data.auth.repository._common

interface AuthDataRepository {
    suspend fun signIn()
    suspend fun signIn(login: String, password: String)
    suspend fun signUp(login: String, password: String)
    suspend fun logout()
}