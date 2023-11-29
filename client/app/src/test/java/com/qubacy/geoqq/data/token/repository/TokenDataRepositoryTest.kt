package com.qubacy.geoqq.data.token.repository

import com.qubacy.geoqq.common.util.mock.Base64MockContext
import com.qubacy.geoqq.common.error.ErrorContext
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.token.repository.result.GetTokensResult
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.network.NetworkTokenDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class TokenDataRepositoryTest() {
    companion object {
        init { Base64MockContext.mockBase64() }
    }

    private var mPreservedRefreshToken: String? = null
    private var mAccessToken: String? = null

    protected lateinit var mTokenDataRepository: TokenDataRepository

    protected fun initTokenDataRepository(
        code: Int = 200,
        responseString: String = String()
    ) {
        val localTokenDataSourceMocked = Mockito.mock(LocalTokenDataSource::class.java)

        Mockito.`when`(localTokenDataSourceMocked.loadRefreshToken()).thenAnswer { mPreservedRefreshToken }
        Mockito.`when`(localTokenDataSourceMocked.accessToken).thenAnswer { mAccessToken }
        Mockito.`when`(
            localTokenDataSourceMocked.saveTokens(
                Mockito.anyString(),
                Mockito.anyString()
            )
        )
            .thenAnswer {
                this.mAccessToken = it.arguments[0] as String
                this.mPreservedRefreshToken = it.arguments[1] as String

                Unit
            }
        Mockito.`when`(localTokenDataSourceMocked.checkTokenForValidity(Mockito.anyString()))
            .thenCallRealMethod()

        val networkTokenDataSource = NetworkTestContext
            .generateTestRetrofit(
                NetworkTestContext.generateDefaultTestInterceptor(code, responseString))
            .create(NetworkTokenDataSource::class.java)

        mTokenDataRepository = TokenDataRepository(
            localTokenDataSourceMocked, networkTokenDataSource)
    }

    @Before
    fun setup() {
        initTokenDataRepository()
    }

    @Test
    fun saveTokensTest() {
        runBlocking {
            val refreshToken = "token"
            val accessToken = "token"

            val result = mTokenDataRepository.saveTokens(refreshToken, accessToken)

            Assert.assertEquals(Result::class, result::class)
        }
    }

    @Test
    fun getTokensWithoutLocalRefreshTokenTest() {
        initTokenDataRepository()

        runBlocking {
            val getTokensResult = mTokenDataRepository.getTokens()

            Assert.assertEquals(ErrorResult::class, getTokensResult::class)
        }
    }

    @Test
    fun getTokensWithInvalidLocalRefreshTokenTest() {
        val refreshToken = "invalid_token"
        val accessToken = "invalid_token"

        initTokenDataRepository()

        runBlocking {
            mTokenDataRepository.saveTokens(refreshToken, accessToken)

            val getTokensResult = mTokenDataRepository.getTokens()

            Assert.assertEquals(ErrorResult::class, getTokensResult::class)
            Assert.assertEquals(
                ErrorContext.Token.LOCAL_REFRESH_TOKEN_INVALID.id,
                (getTokensResult as ErrorResult).errorId
            )
        }
    }

    @Test
    fun getTokensWithInvalidAccessTokenTest() {
        val validRefreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTY5ODcyMjc4MywiZXhwIjoxOTk4NzI2MzgzfQ.-zTDQgcS_MuPK2uQgCEwQKmh1r4u1U3Wd7jqDZTPm38"
        val invalidAccessToken = "invalid_token"

        val updatedRefreshToken = "refresh_token"
        val updatedAccessToken = "access_token"

        val responseString =
            "{\"access-token\":\"$updatedAccessToken\",\"refresh-token\":\"$updatedRefreshToken\"}"

        initTokenDataRepository(200, responseString)

        runBlocking {
            mTokenDataRepository.saveTokens(validRefreshToken, invalidAccessToken)

            val result = mTokenDataRepository.getTokens()

            Assert.assertEquals(GetTokensResult::class, result::class)

            val getTokensResult = result as GetTokensResult

            val currentRefreshTokenFromResult = getTokensResult.refreshToken
            val currentAccessTokenFromResult = getTokensResult.accessToken

            Assert.assertEquals(updatedRefreshToken, currentRefreshTokenFromResult)
            Assert.assertEquals(updatedAccessToken, currentAccessTokenFromResult)

            val currentRefreshToken = mTokenDataRepository.localTokenDataSource.loadRefreshToken()
            val currentAccessToken = mTokenDataRepository.localTokenDataSource.accessToken

            Assert.assertEquals(updatedRefreshToken, currentRefreshToken)
            Assert.assertEquals(updatedAccessToken, currentAccessToken)
        }
    }

    @Test
    fun getTokensWithValidTokens() {
        val validRefreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTY5ODcyMjc4MywiZXhwIjoxOTk4NzI2MzgzfQ.-zTDQgcS_MuPK2uQgCEwQKmh1r4u1U3Wd7jqDZTPm38"
        val validAccessToken = validRefreshToken

        runBlocking {
            mTokenDataRepository.saveTokens(validRefreshToken, validAccessToken)

            val result = mTokenDataRepository.getTokens()

            Assert.assertEquals(GetTokensResult::class, result::class)

            val getTokensResult = result as GetTokensResult

            val currentRefreshTokenFromResult = getTokensResult.refreshToken
            val currentAccessTokenFromResult = getTokensResult.accessToken

            Assert.assertEquals(validRefreshToken, currentRefreshTokenFromResult)
            Assert.assertEquals(validAccessToken, currentAccessTokenFromResult)

            val currentRefreshToken = mTokenDataRepository.localTokenDataSource.loadRefreshToken()
            val currentAccessToken = mTokenDataRepository.localTokenDataSource.accessToken

            Assert.assertEquals(validRefreshToken, currentRefreshToken)
            Assert.assertEquals(validAccessToken, currentAccessToken)
        }
    }
}