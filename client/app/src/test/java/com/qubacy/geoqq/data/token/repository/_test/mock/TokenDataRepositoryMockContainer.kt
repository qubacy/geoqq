package com.qubacy.geoqq.data.token.repository._test.mock

import com.auth0.android.jwt.Claim
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.token.repository.result.GetTokensDataResult
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito

class TokenDataRepositoryMockContainer {
    companion object {
        val DEFAULT_GET_TOKENS_DATA_RESULT = GetTokensDataResult(String(), String())
    }

    val tokenDataRepositoryMock: TokenDataRepository

    var error: Error? = null

    var getTokensDataResult: GetTokensDataResult? = DEFAULT_GET_TOKENS_DATA_RESULT
    var getAccessTokenPayload: Map<String, Claim>? = null

    private var mGetTokensCallFlag = false
    val getTokensCallFlag get() = mGetTokensCallFlag
    private var mGetAccessTokenPayloadCallFlag = false
    val getAccessTokenPayloadCallFlag get() = mGetAccessTokenPayloadCallFlag
    private var mClearTokensCallFlag = false
    val clearTokensCallFlag get() = mClearTokensCallFlag
    private var mSignInWithTokenCallFlag = false
    val signInWithTokenCallFlag get() = mSignInWithTokenCallFlag
    private var mSignInWithLoginDataCallFlag = false
    val signInWithLoginDataCallFlag get() = mSignInWithLoginDataCallFlag
    private var mSignUpCallFlag = false
    val signUpCallFlag get() = mSignUpCallFlag

    init {
        tokenDataRepositoryMock = mockTokenDataRepository()
    }

    fun clear() {
        error = null

        getTokensDataResult = null
        getAccessTokenPayload = null

        mGetTokensCallFlag = false
        mGetAccessTokenPayloadCallFlag = false
        mClearTokensCallFlag = false
        mSignInWithTokenCallFlag = false
        mSignInWithLoginDataCallFlag = false
        mSignUpCallFlag = false
    }

    private fun mockTokenDataRepository(): TokenDataRepository {
        val tokenDataRepositoryMock = Mockito.mock(TokenDataRepository::class.java)

        runTest {
            Mockito.`when`(tokenDataRepositoryMock.getTokens()).thenAnswer {
                mGetTokensCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                getTokensDataResult
            }
            Mockito.`when`(tokenDataRepositoryMock.logout()).thenAnswer {
                mClearTokensCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                Unit
            }
            Mockito.`when`(tokenDataRepositoryMock.signIn()).thenAnswer {
                mSignInWithTokenCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                Unit
            }
            Mockito.`when`(tokenDataRepositoryMock.signIn(
                Mockito.anyString(), Mockito.anyString())
            ).thenAnswer {
                mSignInWithLoginDataCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                Unit
            }
            Mockito.`when`(tokenDataRepositoryMock.signUp(
                Mockito.anyString(), Mockito.anyString()
            )).thenAnswer {
                mSignUpCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                Unit
            }
            Mockito.`when`(tokenDataRepositoryMock.getAccessTokenPayload()).thenAnswer {
                mGetAccessTokenPayloadCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                getAccessTokenPayload
            }
        }

        return tokenDataRepositoryMock
    }
}