package com.qubacy.geoqq.data.token.repository

import com.qubacy.geoqq.data.common.repository.TokenBasedRepositoryTest
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenExistenceResult
import com.qubacy.geoqq.data.token.repository.result.CheckRefreshTokenValidityResult
import com.qubacy.geoqq.data.token.repository.result.GetAccessTokenResult
import com.qubacy.geoqq.data.token.repository.result.UpdateTokensResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TokenDataRepositoryTest() : TokenBasedRepositoryTest() {
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
    fun getAccessTokenTest() {
        runBlocking {
            val refreshToken = "token"
            val accessToken = "token"

            mTokenDataRepository.saveTokens(refreshToken, accessToken)

            val result = mTokenDataRepository.getAccessToken()

            Assert.assertEquals(GetAccessTokenResult::class, result::class)
            Assert.assertEquals(accessToken, (result as GetAccessTokenResult).accessToken)
        }
    }

    @Test
    fun checkRefreshTokenExistenceTest() {
        val refreshToken = "token"
        val accessToken = "token"

        runBlocking {
            mTokenDataRepository.saveTokens(refreshToken, accessToken)

            val result = mTokenDataRepository.checkLocalRefreshTokenExistence()

            Assert.assertEquals(CheckRefreshTokenExistenceResult::class, result::class)
            Assert.assertEquals(true, (result as CheckRefreshTokenExistenceResult).isExisting)
        }
    }

    @Test
    fun checkRefreshTokenValidityTest() {
        val refreshToken = "invalid_token"
        val accessToken = "token"

        runBlocking {
            mTokenDataRepository.saveTokens(refreshToken, accessToken)
            val invalidResult = mTokenDataRepository.checkRefreshTokenValidity()

            Assert.assertEquals(CheckRefreshTokenValidityResult::class, invalidResult::class)
            Assert.assertFalse((invalidResult as CheckRefreshTokenValidityResult).isValid)

            val validRefreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTY5ODcyMjc4MywiZXhwIjoxOTk4NzI2MzgzfQ.-zTDQgcS_MuPK2uQgCEwQKmh1r4u1U3Wd7jqDZTPm38"

            mTokenDataRepository.saveTokens(validRefreshToken, accessToken)
            val validResult = mTokenDataRepository.checkRefreshTokenValidity()

            Assert.assertEquals(CheckRefreshTokenValidityResult::class, validResult::class)
            Assert.assertTrue((validResult as CheckRefreshTokenValidityResult).isValid)
        }
    }

    @Test
    fun updateTokensTest() {
        val refreshToken = "token"
        val accessToken = "token"

        val updatedRefreshToken = "updatedToken"
        val updatedAccessToken = "updatedToken"

        val responseString =
            "{\"access-token\":\"$updatedAccessToken\",\"refresh-token\":\"$updatedRefreshToken\"}"

        initTokenDataRepository(200, responseString)

        runBlocking {
            mTokenDataRepository.saveTokens(refreshToken, accessToken)

            val result = mTokenDataRepository.updateTokens()

            Assert.assertEquals(UpdateTokensResult::class, result::class)

            val updateTokensResult = result as UpdateTokensResult

            val currentRefreshTokenFromResult = updateTokensResult.refreshToken
            val currentAccessTokenFromResult = updateTokensResult.accessToken

            Assert.assertEquals(updatedRefreshToken, currentRefreshTokenFromResult)
            Assert.assertEquals(updatedAccessToken, currentAccessTokenFromResult)

            val currentRefreshToken = mTokenDataRepository.localTokenDataSource.loadRefreshToken()
            val currentAccessToken = mTokenDataRepository.localTokenDataSource.accessToken

            Assert.assertEquals(updatedRefreshToken, currentRefreshToken)
            Assert.assertEquals(updatedAccessToken, currentAccessToken)
        }
    }
}