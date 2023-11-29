package com.qubacy.geoqq.data.signup.repository

import com.qubacy.geoqq.common.util.mock.Base64MockContext
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.signup.repository.result.SignUpResult
import com.qubacy.geoqq.data.signup.repository.source.network.NetworkSignUpDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SignUpDataRepositoryTest(

) {
    companion object {
        init { Base64MockContext.mockBase64() }
    }

    private lateinit var mSignUpDataRepository: SignUpDataRepository

    private fun initSignUpDataRepository(
        code: Int = 200,
        responseString: String = String()
    ) {
        val networkSignUpDataSource = NetworkTestContext.generateTestRetrofit(
            NetworkTestContext.generateDefaultTestInterceptor(code, responseString)
        ).create(NetworkSignUpDataSource::class.java)

        mSignUpDataRepository = SignUpDataRepository(networkSignUpDataSource)
    }

    @Before
    fun setup() {
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

            val signUpResult = result as SignUpResult

            Assert.assertEquals(refreshToken, signUpResult.refreshToken)
            Assert.assertEquals(accessToken, signUpResult.accessToken)
        }
    }
}