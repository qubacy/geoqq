package com.qubacy.geoqq.data.auth.repository.impl

import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._test.mock.WebSocketAdapterMockAdapter
import com.qubacy.geoqq.data._common.repository.token.repository._common.result.get.GetTokensDataResult
import com.qubacy.geoqq.data._common.repository.token.repository._common.result.update.UpdateTokensDataResult
import com.qubacy.geoqq.data._common.repository.token.repository._test.mock.TokenDataRepositoryMockContainer
import com.qubacy.geoqq.data.auth.repository._common._test.context.AuthDataRepositoryMockContext
import com.qubacy.geoqq.data.auth.repository._common.source.local.database._common.LocalAuthDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.RemoteAuthHttpRestDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignInResponse
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignUpResponse
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class AuthDataRepositoryImplTest : DataRepositoryTest<AuthDataRepositoryImpl>() {
    companion object {
        const val DEFAULT_VALID_TOKEN = AuthDataRepositoryMockContext.DEFAULT_VALID_TOKEN

        val DEFAULT_SIGN_IN_RESPONSE = AuthDataRepositoryMockContext.DEFAULT_SIGN_IN_RESPONSE
        val DEFAULT_SIGN_UP_RESPONSE = AuthDataRepositoryMockContext.DEFAULT_SIGN_UP_RESPONSE
    }

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataSourceMockContainer
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer
    private lateinit var mWebSocketAdapterMockContainer: WebSocketAdapterMockAdapter

    private var mLocalDatabaseSourceDropDataTablesCallFlag = false

    private var mHttpSourceSignInResponse: SignInResponse? = null
    private var mHttpSourceSignUpResponse: SignUpResponse? = null

    private var mHttpSourceSignInCallFlag = false
    private var mHttpSourceSignUpCallFlag = false

    @Before
    fun setup() {
        initTokenDataRepository()
    }

    @After
    fun clear() {
        mLocalDatabaseSourceDropDataTablesCallFlag = false

        mHttpSourceSignInResponse = null
        mHttpSourceSignUpResponse = null

        mHttpSourceSignInCallFlag = false
        mHttpSourceSignUpCallFlag = false
    }

    private fun initTokenDataRepository() = runTest {
        mErrorDataRepositoryMockContainer = ErrorDataSourceMockContainer()
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()
        mWebSocketAdapterMockContainer = WebSocketAdapterMockAdapter()

        val localAuthDatabaseDataSourceMock = mockLocalAuthDatabaseDataSource()
        val httpAuthDataSourceMock = mockHttpAuthDataSource()

        mDataRepository = AuthDataRepositoryImpl(
            mErrorDataRepositoryMockContainer.errorDataSourceMock,
            localAuthDatabaseDataSourceMock,
            httpAuthDataSourceMock,
            mTokenDataRepositoryMockContainer.tokenDataRepository,
            mWebSocketAdapterMockContainer.webSocketAdapter
        )
    }

    private fun mockLocalAuthDatabaseDataSource(): LocalAuthDatabaseDataSource {
        val localAuthDatabaseDataSourceMock = Mockito.mock(LocalAuthDatabaseDataSource::class.java)

        Mockito.`when`(localAuthDatabaseDataSourceMock.dropDataTables()).thenAnswer {
            mLocalDatabaseSourceDropDataTablesCallFlag = true

            Unit
        }

        return localAuthDatabaseDataSourceMock
    }

    private fun mockHttpAuthDataSource(): RemoteAuthHttpRestDataSource {
        val httpAuthDataSourceMock = Mockito.mock(RemoteAuthHttpRestDataSource::class.java)

        Mockito.`when`(httpAuthDataSourceMock.signIn(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mHttpSourceSignInCallFlag = true
            mHttpSourceSignInResponse
        }

        Mockito.`when`(httpAuthDataSourceMock.signUp(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mHttpSourceSignUpCallFlag = true
            mHttpSourceSignUpResponse
        }

        return httpAuthDataSourceMock
    }

    @Test
    fun signInWithTokenTest() = runTest {
        val getTokensDataResult = GetTokensDataResult(DEFAULT_VALID_TOKEN, DEFAULT_VALID_TOKEN)
        val updateTokensDataResult = UpdateTokensDataResult(DEFAULT_VALID_TOKEN, DEFAULT_VALID_TOKEN)

        mTokenDataRepositoryMockContainer.getTokensDataResult = getTokensDataResult
        mTokenDataRepositoryMockContainer.updateTokensDataResult = updateTokensDataResult

        mDataRepository.signIn()

        Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)
        Assert.assertTrue(mTokenDataRepositoryMockContainer.updateTokensCallFlag)
    }

    @Test
    fun signInWithLoginDataTest() = runTest {
        val login = "login"
        val password = "password"

        val signInResponse = DEFAULT_SIGN_IN_RESPONSE

        mHttpSourceSignInResponse = signInResponse

        mDataRepository.signIn(login, password)

        Assert.assertTrue(mHttpSourceSignInCallFlag)
        Assert.assertTrue(mTokenDataRepositoryMockContainer.saveTokensCallFlag)
    }

    @Test
    fun signUpTest() = runTest {
        val login = "login"
        val password = "password"

        val signUpResponse = DEFAULT_SIGN_UP_RESPONSE

        mHttpSourceSignUpResponse = signUpResponse

        mDataRepository.signUp(login, password)

        Assert.assertTrue(mHttpSourceSignUpCallFlag)
        Assert.assertTrue(mTokenDataRepositoryMockContainer.saveTokensCallFlag)
    }

    @Test
    fun logoutTest() = runTest {
        mDataRepository.logout()

        Assert.assertTrue(mTokenDataRepositoryMockContainer.resetCallFlag)
        Assert.assertTrue(mLocalDatabaseSourceDropDataTablesCallFlag)
    }
}