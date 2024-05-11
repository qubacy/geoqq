package com.qubacy.geoqq.data.auth.repository._test.mock

import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito

class AuthDataRepositoryMockContainer {
    val authDataRepositoryMock: AuthDataRepositoryImpl

    var error: Error? = null

    private var mSignInWithTokenCallFlag = false
    val signInWithTokenCallFlag get() = mSignInWithTokenCallFlag

    private var mSignInWithLoginDataCallFlag = false
    val signInWithLoginDataCallFlag get() = mSignInWithLoginDataCallFlag

    private var mSignUpCallFlag = false
    val signUpCallFlag get() = mSignUpCallFlag

    private var mLogoutCallFlag = false
    val logoutCallFlag get() = mLogoutCallFlag

    init {
        authDataRepositoryMock = mockTokenDataRepository()
    }

    fun clear() {
        error = null

        mSignInWithTokenCallFlag = false
        mSignInWithLoginDataCallFlag = false
        mSignUpCallFlag = false
    }

    private fun mockTokenDataRepository(): AuthDataRepositoryImpl {
        val authDataRepositoryMock = Mockito.mock(AuthDataRepositoryImpl::class.java)

        runTest {
            Mockito.`when`(authDataRepositoryMock.logout()).thenAnswer {
                mLogoutCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                Unit
            }
            Mockito.`when`(authDataRepositoryMock.signIn()).thenAnswer {
                mSignInWithTokenCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                Unit
            }
            Mockito.`when`(authDataRepositoryMock.signIn(
                Mockito.anyString(), Mockito.anyString())
            ).thenAnswer {
                mSignInWithLoginDataCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                Unit
            }
            Mockito.`when`(authDataRepositoryMock.signUp(
                Mockito.anyString(), Mockito.anyString()
            )).thenAnswer {
                mSignUpCallFlag = true

                if (error != null) throw ErrorAppException(error!!)

                Unit
            }
        }

        return authDataRepositoryMock
    }
}