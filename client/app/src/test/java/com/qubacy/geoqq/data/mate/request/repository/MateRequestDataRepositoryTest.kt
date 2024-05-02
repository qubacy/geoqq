package com.qubacy.geoqq.data.mate.request.repository

import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository.source.remote.http.executor.mock.HttpCallExecutorMockContainer
import com.qubacy.geoqq.data.error.repository._test.mock.ErrorDataRepositoryMockContainer
import com.qubacy.geoqq.data.mate.request.model.toDataMateRequest
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.HttpMateRequestDataSourceApi
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestsResponse
import com.qubacy.geoqq.data.auth.repository._test.mock.TokenDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.repository._test.mock.UserDataRepositoryMockContainer
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Call

class MateRequestDataRepositoryTest : DataRepositoryTest<MateRequestDataRepository>() {
    companion object {
        val DEFAULT_USER = UserDataRepositoryMockContainer.DEFAULT_DATA_USER
        val DEFAULT_MATE_REQUEST = GetMateRequestResponse(0, DEFAULT_USER.id)
    }

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataRepositoryMockContainer
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer
    private lateinit var mHttpCallExecutorMockContainer: HttpCallExecutorMockContainer

    private var mHttpSourceGetMateRequestsCallFlag = false
    private var mHttpSourceGetMateRequestCountCallFlag = false
    private var mHttpSourcePostMateRequestCallFlag = false
    private var mHttpSourceAnswerMateRequestCallFlag = false

    @Before
    fun setup() {
        initMateRequestDataRepository()
    }

    @After
    fun clear() {
        mHttpSourceGetMateRequestsCallFlag = false
        mHttpSourceGetMateRequestCountCallFlag = false
        mHttpSourcePostMateRequestCallFlag = false
        mHttpSourceAnswerMateRequestCallFlag = false
    }

    private fun initMateRequestDataRepository() {
        mErrorDataRepositoryMockContainer = ErrorDataRepositoryMockContainer()
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()
        mHttpCallExecutorMockContainer = HttpCallExecutorMockContainer()

        val httpMateRequestDataSourceMock = mockHttpMateRequestDataSource()

        mDataRepository = MateRequestDataRepository(
            mErrorDataRepository = mErrorDataRepositoryMockContainer.errorDataRepositoryMock,
            mTokenDataRepository = mTokenDataRepositoryMockContainer.tokenDataRepositoryMock,
            mUserDataRepository = mUserDataRepositoryMockContainer.userDataRepository,
            mHttpMateRequestDataSource = httpMateRequestDataSourceMock,
            mHttpCallExecutor = mHttpCallExecutorMockContainer.httpCallExecutor
        )
    }

    private fun mockHttpMateRequestDataSource(): HttpMateRequestDataSourceApi {
        val getMateRequestsCallMock = Mockito.mock(Call::class.java)
        val getMateRequestCountCallMock = Mockito.mock(Call::class.java)
        val postMateRequestCallMock = Mockito.mock(Call::class.java)
        val answerMateRequestCallMock = Mockito.mock(Call::class.java)

        val httpMateRequestDataSourceMock = Mockito.mock(HttpMateRequestDataSourceApi::class.java)

        Mockito.`when`(httpMateRequestDataSourceMock.getMateRequests(
            Mockito.anyInt(),
            Mockito.anyInt(),
            Mockito.anyString()
        )).thenAnswer {
            mHttpSourceGetMateRequestsCallFlag = true
            getMateRequestsCallMock
        }
        Mockito.`when`(httpMateRequestDataSourceMock.getMateRequestCount(
            Mockito.anyString()
        )).thenAnswer {
            mHttpSourceGetMateRequestCountCallFlag = true
            getMateRequestCountCallMock
        }
        Mockito.`when`(httpMateRequestDataSourceMock.postMateRequest(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mHttpSourcePostMateRequestCallFlag = true
            postMateRequestCallMock
        }
        Mockito.`when`(httpMateRequestDataSourceMock.answerMateRequest(
            Mockito.anyLong(),
            Mockito.anyString(),
            Mockito.anyBoolean()
        )).thenAnswer {
            mHttpSourceAnswerMateRequestCallFlag = true
            answerMateRequestCallMock
        }

        return httpMateRequestDataSourceMock
    }

    @Test
    fun getMateRequestsTest() = runTest {
        val offset = 0
        val count = 5
        val getMateRequestsResponse = GetMateRequestsResponse(listOf(
            DEFAULT_MATE_REQUEST
        ))
        val resolveUsers = mapOf(
            DEFAULT_USER.id to DEFAULT_USER
        )

        val expectedDataMateRequests = getMateRequestsResponse.requests.map {
            it.toDataMateRequest(resolveUsers[it.userId]!!)
        }

        mHttpCallExecutorMockContainer.response = getMateRequestsResponse
        mUserDataRepositoryMockContainer.resolveUsers = resolveUsers

        val getMateRequestsResult = mDataRepository.getMateRequests(offset, count)
        val gottenDataMateRequests = getMateRequestsResult.requests

        Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)
        Assert.assertTrue(mHttpSourceGetMateRequestsCallFlag)
        Assert.assertTrue(mHttpCallExecutorMockContainer.executeNetworkRequestCallFlag)
        Assert.assertTrue(mUserDataRepositoryMockContainer.resolveUsersCallFlag)
        AssertUtils.assertEqualContent(expectedDataMateRequests, gottenDataMateRequests)
    }

    @Test
    fun getMateRequestCountTest() = runTest {
        val getMateRequestCountResponse = GetMateRequestCountResponse(5)

        val expectedMateRequestCount = getMateRequestCountResponse.count

        mHttpCallExecutorMockContainer.response = getMateRequestCountResponse

        val getMateRequestCountResult = mDataRepository.getMateRequestCount()
        val gottenDataMateRequestCount = getMateRequestCountResult.count

        Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)
        Assert.assertTrue(mHttpSourceGetMateRequestCountCallFlag)
        Assert.assertTrue(mHttpCallExecutorMockContainer.executeNetworkRequestCallFlag)
        Assert.assertEquals(expectedMateRequestCount, gottenDataMateRequestCount)
    }

    @Test
    fun createMateRequestTest() = runTest {
        val userId = DEFAULT_USER.id

        mDataRepository.createMateRequest(userId)

        Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)
        Assert.assertTrue(mHttpSourcePostMateRequestCallFlag)
        Assert.assertTrue(mHttpCallExecutorMockContainer.executeNetworkRequestCallFlag)
    }

    @Test
    fun answerMateRequestTest() = runTest {
        val id = 0L
        val isAccepted = true

        mDataRepository.answerMateRequest(id, isAccepted)

        Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)
        Assert.assertTrue(mHttpSourceAnswerMateRequestCallFlag)
        Assert.assertTrue(mHttpCallExecutorMockContainer.executeNetworkRequestCallFlag)
    }
}