package com.qubacy.geoqq.data.token.repository._test.mock

import com.auth0.android.jwt.Claim
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensDataResult
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito

class TokenDataRepositoryMockContainer {
    companion object {
        val DEFAULT_GET_TOKENS_DATA_RESULT = GetTokensDataResult(String(), String())
    }

    val tokenDataRepositoryMock: TokenDataRepository

    var getTokensDataResult: GetTokensDataResult? = DEFAULT_GET_TOKENS_DATA_RESULT
    var getAccessTokenPayload: Map<String, Claim>? = null

    private var mGetTokensCallFlag = false
    val getTokensCallFlag get() = mGetTokensCallFlag
    private var mGetAccessTokenPayloadCallFlag = false
    val getAccessTokenPayloadCallFlag get() = mGetAccessTokenPayloadCallFlag
    private var mClearTokensCallFlag = false
    val clearTokensCallFlag get() = mClearTokensCallFlag
    private var mSignInCallFlag = false
    val signInCallFlag get() = mSignInCallFlag
    private var mSignUpCallFlag = false
    val signUpCallFlag get() = mSignUpCallFlag

    init {
        tokenDataRepositoryMock = mockTokenDataRepository()
    }

    private fun mockTokenDataRepository(): TokenDataRepository {
        val tokenDataRepositoryMock = Mockito.mock(TokenDataRepository::class.java)

        runTest {
            Mockito.`when`(tokenDataRepositoryMock.getTokens()).thenAnswer {
                mGetTokensCallFlag = true
                getTokensDataResult
            }
            Mockito.`when`(tokenDataRepositoryMock.clearTokens()).thenAnswer {
                mClearTokensCallFlag = true

                Unit
            }
            Mockito.`when`(tokenDataRepositoryMock.signIn(
                Mockito.anyString(), Mockito.anyString())
            ).thenAnswer {
                mSignInCallFlag = true

                Unit
            }
            Mockito.`when`(tokenDataRepositoryMock.signUp(
                Mockito.anyString(), Mockito.anyString()
            )).thenAnswer {
                mSignUpCallFlag = true

                Unit
            }
            Mockito.`when`(tokenDataRepositoryMock.getAccessTokenPayload()).thenAnswer {
                mGetAccessTokenPayloadCallFlag = true
                getAccessTokenPayload
            }
        }

        return tokenDataRepositoryMock
    }

    fun reset() {
        getTokensDataResult = DEFAULT_GET_TOKENS_DATA_RESULT
        getAccessTokenPayload = null

        mGetTokensCallFlag = false
        mGetAccessTokenPayloadCallFlag = false
        mClearTokensCallFlag = false
        mSignInCallFlag = false
        mSignUpCallFlag = false
    }
}