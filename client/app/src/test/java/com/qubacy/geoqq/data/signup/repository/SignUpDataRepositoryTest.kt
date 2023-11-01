package com.qubacy.geoqq.data.signup.repository

import com.qubacy.geoqq.data.common.repository.TokenBasedRepositoryTest
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.signup.repository.result.SignUpResult
import com.qubacy.geoqq.data.signup.repository.source.network.NetworkSignUpDataSource
import com.qubacy.geoqq.data.token.repository.result.GetAccessTokenResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SignUpDataRepositoryTest(

) : TokenBasedRepositoryTest() {
    private lateinit var mSignUpDataRepository: SignUpDataRepository

    private fun initSignUpDataRepository(
        code: Int = 200,
        responseString: String = String()
    ) {
        val networkSignUpDataSource = NetworkTestContext.generateTestRetrofit(
            NetworkTestContext.generateDefaultTestInterceptor(code, responseString)
        ).create(NetworkSignUpDataSource::class.java)

        mSignUpDataRepository = SignUpDataRepository(mTokenDataRepository, networkSignUpDataSource)
    }

    @Before
    fun setup() {
        initTokenDataRepository()
        initSignUpDataRepository()
    }

    @Test
    fun signUpTest() {
        val login = "testtest"
        val password = "password"

        val accessToken = "token"
        val refreshToken = "token"

        val responseString =
            "{\"access-token\":\"$accessToken\",\"refresh-token\":\"$refreshToken\"}"

        initSignUpDataRepository(200, responseString)

        runBlocking {
            val result = mSignUpDataRepository.signUp(login, password)

            Assert.assertEquals(SignUpResult::class, result::class)

            val getAccessTokenResult = mTokenDataRepository.getAccessToken() as GetAccessTokenResult

            val currentRefreshToken = mTokenDataRepository.localTokenDataSource.loadRefreshToken()
            val currentAccessToken = getAccessTokenResult.accessToken

            Assert.assertEquals(refreshToken, currentRefreshToken)
            Assert.assertEquals(accessToken, currentAccessToken)
        }
    }
}