package com.qubacy.geoqq.data.geo.message.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessageResponse
import com.qubacy.geoqq.data._common.repository.message.source.remote.http.response.GetMessagesResponse
import com.qubacy.geoqq.data.geo.message.repository.source.http.HttpGeoMessageDataSource
import com.qubacy.geoqq.data.user.repository._test.mock.UserDataRepositoryMockContainer
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class GeoMessageDataRepositoryTest : DataRepositoryTest<GeoMessageDataRepository>() {
    @get:Rule
    val rule = RuleChain
        .outerRule(MainDispatcherRule())
        .around(InstantTaskExecutorRule())

    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer

    private var mHttpSourceGetMessagesResponse: GetMessagesResponse? = null

    private var mHttpSourceGetMessagesCallFlag = false
    private var mHttpSourceSendMessageCallFlag = false

    @Before
    fun setup() {
        mDataRepository = initGeoMessageDataRepository()
    }

    private fun initGeoMessageDataRepository(): GeoMessageDataRepository {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()

        val httpGeoMessageDataSourceMock = mockHttpGeoMessageDataSource()

        return GeoMessageDataRepository(
            mErrorSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            mUserDataRepository = mUserDataRepositoryMockContainer.userDataRepository,
            mHttpGeoMessageDataSource = httpGeoMessageDataSourceMock
        )
    }

    private fun mockHttpGeoMessageDataSource(): HttpGeoMessageDataSource {
        val httpGeoMessageDataSourceMock = Mockito.mock(HttpGeoMessageDataSource::class.java)

        Mockito.`when`(httpGeoMessageDataSourceMock.getMessages(
            Mockito.anyInt(), Mockito.anyFloat(), Mockito.anyFloat()
        )).thenAnswer {
            mHttpSourceGetMessagesCallFlag = true
            mHttpSourceGetMessagesResponse
        }
        Mockito.`when`(httpGeoMessageDataSourceMock.sendMessage(
            Mockito.anyString(), Mockito.anyInt(), Mockito.anyFloat(), Mockito.anyFloat()
        )).thenAnswer {
            mHttpSourceSendMessageCallFlag = true

            Unit
        }

        return httpGeoMessageDataSourceMock
    }

    @Test
    fun getMessagesTest() = runTest {
        val radius = 0
        val longitude = 0f
        val latitude = 0f
        val userId = 0L

        val resolveUsersResult =
            UserDataRepositoryMockContainer.DEFAULT_RESOLVE_USERS_WITH_LOCAL_USER
        val getMessagesResponse = GetMessagesResponse(listOf(
            GetMessageResponse(1, userId, "test 2", 1),
            GetMessageResponse(0, userId, "test 1", 0)
        ))

        val dataUser = resolveUsersResult.userIdUserMap[userId]!!

        val expectedDataMessages = getMessagesResponse.messages.map { it.toDataMessage(dataUser) }

        mHttpSourceGetMessagesResponse = getMessagesResponse
        mUserDataRepositoryMockContainer.resolveUsersWithLocalUserResult = resolveUsersResult

        val getMessagesResultLiveData = mDataRepository.getMessages(radius, longitude, latitude)
        val getMessagesResult =getMessagesResultLiveData.await()

        val gottenDatMessages = getMessagesResult.messages

        Assert.assertTrue(mUserDataRepositoryMockContainer.resolveUsersWithLocalUserCallFlag)
        Assert.assertTrue(mHttpSourceGetMessagesCallFlag)
        AssertUtils.assertEqualContent(expectedDataMessages, gottenDatMessages)
    }

    @Test
    fun sendMessageTest() = runTest {
        val text = "test"
        val radius = 0
        val longitude = 0f
        val latitude = 0f

        mDataRepository.sendMessage(text, radius, longitude, latitude)

        Assert.assertTrue(mHttpSourceSendMessageCallFlag)
    }
}