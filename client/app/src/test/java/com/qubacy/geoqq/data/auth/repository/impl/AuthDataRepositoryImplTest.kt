package com.qubacy.geoqq.data.auth.repository.impl

import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.api.response.UpdateTokensResponse
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._common._test.mock.LocalTokenDataStoreDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository.token.repository._common.source.remote.http.rest._common.RemoteTokenHttpRestDataSource
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

        val DEFAULT_UPDATE_TOKENS_RESPONSE = AuthDataRepositoryMockContext
            .DEFAULT_UPDATE_TOKENS_RESPONSE
        val DEFAULT_SIGN_IN_RESPONSE = AuthDataRepositoryMockContext.DEFAULT_SIGN_IN_RESPONSE
        val DEFAULT_SIGN_UP_RESPONSE = AuthDataRepositoryMockContext.DEFAULT_SIGN_UP_RESPONSE
    }

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataSourceMockContainer
    private lateinit var mLocalTokenDataStoreDataSourceMockContainer:
            LocalTokenDataStoreDataSourceMockContainer

    private var mLocalDatabaseSourceDropDataTablesCallFlag = false

    private var mHttpSourceUpdateTokensResponse: UpdateTokensResponse? = null

    private var mHttpSourceUpdateTokensCallFlag = false

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

        mHttpSourceUpdateTokensResponse = null

        mHttpSourceUpdateTokensCallFlag = false

        mHttpSourceSignInResponse = null
        mHttpSourceSignUpResponse = null

        mHttpSourceSignInCallFlag = false
        mHttpSourceSignUpCallFlag = false
    }

    private fun initTokenDataRepository() = runTest {
        mErrorDataRepositoryMockContainer = ErrorDataSourceMockContainer()
        mLocalTokenDataStoreDataSourceMockContainer = LocalTokenDataStoreDataSourceMockContainer()

        val localAuthDatabaseDataSourceMock = mockLocalAuthDatabaseDataSource()

        val httpTokenDataSourceMock = mockHttpTokenDataSource()
        val httpAuthDataSourceMock = mockHttpAuthDataSource()

        mDataRepository = AuthDataRepositoryImpl(
            mErrorDataRepositoryMockContainer.errorDataSourceMock,
            mLocalTokenDataStoreDataSourceMockContainer.localTokenDataStoreDataSourceMock,
            localAuthDatabaseDataSourceMock,
            httpTokenDataSourceMock,
            httpAuthDataSourceMock
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

    private fun mockHttpTokenDataSource(): RemoteTokenHttpRestDataSource {
        val remoteTokenHttpRestDataSourceMock = Mockito.mock(RemoteTokenHttpRestDataSource::class.java)

        Mockito.`when`(remoteTokenHttpRestDataSourceMock.updateTokens(
            Mockito.anyString()
        )).thenAnswer {
            mHttpSourceUpdateTokensCallFlag = true
            mHttpSourceUpdateTokensResponse
        }

        return remoteTokenHttpRestDataSourceMock
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
        val refreshToken = DEFAULT_VALID_TOKEN
        val updateTokensResponse = DEFAULT_UPDATE_TOKENS_RESPONSE

        mLocalTokenDataStoreDataSourceMockContainer.getRefreshToken = refreshToken
        mHttpSourceUpdateTokensResponse = updateTokensResponse

        mDataRepository.signIn()

        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.getRefreshTokenCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.saveTokensCallFlag)
    }

    @Test
    fun signInWithLoginDataTest() = runTest {
        val login = "login"
        val password = "password"

        val signInResponse = DEFAULT_SIGN_IN_RESPONSE

        mHttpSourceSignInResponse = signInResponse

        mDataRepository.signIn(login, password)

        Assert.assertTrue(mHttpSourceSignInCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.saveTokensCallFlag)
    }

    @Test
    fun signUpTest() = runTest {
        val login = "login"
        val password = "password"

        val signUpResponse = DEFAULT_SIGN_UP_RESPONSE

        mHttpSourceSignUpResponse = signUpResponse

        mDataRepository.signUp(login, password)

        Assert.assertTrue(mHttpSourceSignUpCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.saveTokensCallFlag)
    }

    @Test
    fun logoutTest() = runTest {
        mDataRepository.logout()

        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.clearTokensCallFlag)
        Assert.assertTrue(mLocalDatabaseSourceDropDataTablesCallFlag)
    }
}