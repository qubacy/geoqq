package com.qubacy.geoqq.data.signin.repository

import com.qubacy.geoqq.common.util.mock.Base64MockContext
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.signin.repository.result.SignInWithLoginPasswordResult
import com.qubacy.geoqq.data.signin.repository.source.network.NetworkSignInDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SignInDataRepositoryTest(

) {
    companion object {
        init { Base64MockContext.mockBase64() }
    }

    private lateinit var mSignInDataRepository: SignInDataRepository

    private fun initSignInDataRepository(
        code: Int = 200,
        responseString: String = String()
    ) {
        val networkSignInDataSource = NetworkTestContext.generateTestRetrofit(
            NetworkTestContext.generateDefaultTestInterceptor(code, responseString)
        ).create(NetworkSignInDataSource::class.java)

        mSignInDataRepository = SignInDataRepository(networkSignInDataSource)
    }

    @Before
    fun setup() {
        initSignInDataRepository()
    }

    @Test
    fun signInWithUsernamePasswordTest() {
        val login = "testtest"
        val password = "password"

        val refreshToken = "updatedToken"
        val accessToken = "updatedToken"

        val responseString =
            "{\"access-token\":\"$accessToken\",\"refresh-token\":\"$refreshToken\"}"

        initSignInDataRepository(200, responseString)

        runBlocking {
            val result = mSignInDataRepository.signInWithLoginPassword(login, password)

            Assert.assertEquals(SignInWithLoginPasswordResult::class, result::class)

            val signInResult = (result as SignInWithLoginPasswordResult)

            Assert.assertEquals(refreshToken, signInResult.refreshToken)
            Assert.assertEquals(accessToken, signInResult.accessToken)
        }
    }
}