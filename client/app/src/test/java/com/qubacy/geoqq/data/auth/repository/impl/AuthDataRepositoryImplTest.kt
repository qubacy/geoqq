package com.qubacy.geoqq.data.auth.repository.impl

import app.cash.turbine.test
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.error._test.TestError
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.error.WebSocketErrorResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket.adapter._test.mock.WebSocketAdapterMockAdapter
import com.qubacy.geoqq.data._common.repository.token.repository._common.result.get.GetTokensDataResult
import com.qubacy.geoqq.data._common.repository.token.repository._common.result.update.UpdateTokensDataResult
import com.qubacy.geoqq.data._common.repository.token.repository._test.mock.TokenDataRepositoryMockContainer
import com.qubacy.geoqq.data.auth.repository._common._test.context.AuthDataRepositoryMockContext
import com.qubacy.geoqq.data.auth.repository._common.source.local.database._common.LocalAuthDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.RemoteAuthHttpRestDataSource
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignInResponse
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.rest._common.api.response.SignUpResponse
import com.qubacy.geoqq.data.auth.repository._common.source.remote.http.websocket._common.RemoteAuthHttpWebSocketDataSource
import kotlinx.coroutines.flow.MutableSharedFlow
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

    private var mRemoteHttpRestSourceSignInResponse: SignInResponse? = null
    private var mRemoteHttpRestSourceSignUpResponse: SignUpResponse? = null

    private var mRemoteHttpRestSourceSignInCallFlag = false
    private var mRemoteHttpRestSourceSignUpCallFlag = false

    private val mRemoteHttpWebSocketSourceEventFlow: MutableSharedFlow<WebSocketResult> =
        MutableSharedFlow()

    private var mRemoteHttpWebSocketSourceStartProducingCallFlag = false
    private var mRemoteHttpWebSocketSourceStopProducingCallFlag = false
    private var mRemoteHttpWebSocketSourceSetWebSocketAdapterCallFlag = false

    @Before
    fun setup() {
        initTokenDataRepository()
    }

    @After
    fun clear() {
        mLocalDatabaseSourceDropDataTablesCallFlag = false

        mRemoteHttpRestSourceSignInResponse = null
        mRemoteHttpRestSourceSignUpResponse = null

        mRemoteHttpRestSourceSignInCallFlag = false
        mRemoteHttpRestSourceSignUpCallFlag = false

        mRemoteHttpWebSocketSourceStartProducingCallFlag = false
        mRemoteHttpWebSocketSourceStopProducingCallFlag = false
        mRemoteHttpWebSocketSourceSetWebSocketAdapterCallFlag = false
    }

    private fun initTokenDataRepository() = runTest {
        mErrorDataRepositoryMockContainer = ErrorDataSourceMockContainer()
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()
        mWebSocketAdapterMockContainer = WebSocketAdapterMockAdapter()

        val localAuthDatabaseDataSourceMock = mockLocalAuthDatabaseDataSource()
        val remoteAuthHttpRestDataSourceMock = mockRemoteAuthHttpRestDataSource()
        val remoteAuthHttpWebSocketDataSourceMock = mockRemoteAuthHttpWebSocketDataSource()

        mDataRepository = AuthDataRepositoryImpl(
            mErrorSource = mErrorDataRepositoryMockContainer.errorDataSourceMock,
            mLocalAuthDatabaseDataSource = localAuthDatabaseDataSourceMock,
            mRemoteAuthHttpRestDataSource = remoteAuthHttpRestDataSourceMock,
            mRemoteAuthHttpWebSocketDataSource = remoteAuthHttpWebSocketDataSourceMock,
            mTokenDataRepository = mTokenDataRepositoryMockContainer.tokenDataRepository,
            mWebSocketAdapter = mWebSocketAdapterMockContainer.webSocketAdapter
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

    private fun mockRemoteAuthHttpRestDataSource(): RemoteAuthHttpRestDataSource {
        val httpAuthDataSourceMock = Mockito.mock(RemoteAuthHttpRestDataSource::class.java)

        Mockito.`when`(httpAuthDataSourceMock.signIn(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mRemoteHttpRestSourceSignInCallFlag = true
            mRemoteHttpRestSourceSignInResponse
        }

        Mockito.`when`(httpAuthDataSourceMock.signUp(
            Mockito.anyString(), Mockito.anyString()
        )).thenAnswer {
            mRemoteHttpRestSourceSignUpCallFlag = true
            mRemoteHttpRestSourceSignUpResponse
        }

        return httpAuthDataSourceMock
    }

    private fun mockRemoteAuthHttpWebSocketDataSource(): RemoteAuthHttpWebSocketDataSource {
        val remoteAuthHttpWebSocketDataSource = Mockito
            .mock(RemoteAuthHttpWebSocketDataSource::class.java)

        Mockito.`when`(remoteAuthHttpWebSocketDataSource.startProducing()).thenAnswer {
            mRemoteHttpWebSocketSourceStartProducingCallFlag = true

            Unit
        }
        Mockito.`when`(remoteAuthHttpWebSocketDataSource.stopProducing()).thenAnswer {
            mRemoteHttpWebSocketSourceStopProducingCallFlag = true

            Unit
        }
        Mockito.`when`(remoteAuthHttpWebSocketDataSource.setWebSocketAdapter(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mRemoteHttpWebSocketSourceSetWebSocketAdapterCallFlag = true

            Unit
        }
        Mockito.`when`(remoteAuthHttpWebSocketDataSource.eventFlow).thenAnswer {
            mRemoteHttpWebSocketSourceEventFlow
        }

        return remoteAuthHttpWebSocketDataSource
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
        Assert.assertTrue(mWebSocketAdapterMockContainer.openCallFlag)
    }

    @Test
    fun signInWithLoginDataTest() = runTest {
        val login = "login"
        val password = "password"

        val signInResponse = DEFAULT_SIGN_IN_RESPONSE

        mRemoteHttpRestSourceSignInResponse = signInResponse

        mDataRepository.signIn(login, password)

        Assert.assertTrue(mRemoteHttpRestSourceSignInCallFlag)
        Assert.assertTrue(mTokenDataRepositoryMockContainer.saveTokensCallFlag)
        Assert.assertTrue(mWebSocketAdapterMockContainer.openCallFlag)
    }

    @Test
    fun signUpTest() = runTest {
        val login = "login"
        val password = "password"

        val signUpResponse = DEFAULT_SIGN_UP_RESPONSE

        mRemoteHttpRestSourceSignUpResponse = signUpResponse

        mDataRepository.signUp(login, password)

        Assert.assertTrue(mRemoteHttpRestSourceSignUpCallFlag)
        Assert.assertTrue(mTokenDataRepositoryMockContainer.saveTokensCallFlag)
        Assert.assertTrue(mWebSocketAdapterMockContainer.openCallFlag)
    }

    @Test
    fun logoutTest() = runTest {
        mDataRepository.logout()

        Assert.assertTrue(mTokenDataRepositoryMockContainer.resetCallFlag)
        Assert.assertTrue(mLocalDatabaseSourceDropDataTablesCallFlag)
        Assert.assertTrue(mWebSocketAdapterMockContainer.closeCallFlag)
    }

    @Test
    fun processWebSocketErrorResultTest() = runTest {
        val error = TestError.normal
        val webSocketErrorResult = WebSocketErrorResult(error)

        val expectedException = ErrorAppException(error)

        mDataRepository.resultFlow.test {
            mRemoteHttpWebSocketSourceEventFlow.emit(webSocketErrorResult)

            val gottenException = awaitError()

            Assert.assertEquals(expectedException, gottenException)
        }
    }
}