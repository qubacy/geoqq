package com.qubacy.geoqq.data.common.repository

import com.qubacy.geoqq.common.Base64MockContext
import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.source.local.LocalTokenDataSource
import com.qubacy.geoqq.data.token.repository.source.network.NetworkTokenDataSource
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

abstract class TokenBasedRepositoryTest(

) {
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
        val localTokenDataSourceMocked = mock(LocalTokenDataSource::class.java)

        `when`(localTokenDataSourceMocked.loadRefreshToken()).thenAnswer { mPreservedRefreshToken }
        `when`(localTokenDataSourceMocked.accessToken).thenAnswer { mAccessToken }
        `when`(localTokenDataSourceMocked.saveTokens(anyString(), anyString()))
            .thenAnswer {
                this@TokenBasedRepositoryTest.mAccessToken = it.arguments[0] as String
                this@TokenBasedRepositoryTest.mPreservedRefreshToken = it.arguments[1] as String

                Unit
            }
        `when`(localTokenDataSourceMocked.checkRefreshTokenForValidity(anyString()))
            .thenCallRealMethod()

        val networkTokenDataSource = NetworkTestContext
            .generateTestRetrofit(
                NetworkTestContext.generateDefaultTestInterceptor(code, responseString))
            .create(NetworkTokenDataSource::class.java)

        mTokenDataRepository = TokenDataRepository(
            localTokenDataSourceMocked, networkTokenDataSource)
    }
}