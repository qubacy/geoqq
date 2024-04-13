package com.qubacy.geoqq.data.mate.request.repository

import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.util.http.executor._test.mock.OkHttpClientMockContainer
import com.qubacy.geoqq.data.error.repository._test.mock.ErrorDataRepositoryMockContainer
import com.qubacy.geoqq.data.mate.request.model.toDataMateRequest
import com.qubacy.geoqq.data.mate.request.repository.source.http.HttpMateRequestDataSource
import com.qubacy.geoqq.data.mate.request.repository.source.http.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.response.GetMateRequestResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.response.GetMateRequestsResponse
import com.qubacy.geoqq.data.token.repository._test.mock.TokenDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.repository._test.mock.UserDataRepositoryMockContainer
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Call
import retrofit2.Response

class MateRequestDataRepositoryTest : DataRepositoryTest<MateRequestDataRepository>() {
    companion object {
        val DEFAULT_USER = UserDataRepositoryMockContainer.DEFAULT_DATA_USER
        val DEFAULT_MATE_REQUEST = GetMateRequestResponse(0, DEFAULT_USER.id)
    }

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataRepositoryMockContainer
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer
    private lateinit var mOkHttpClientMockContainer: OkHttpClientMockContainer

    private var mHttpSourceGetMateRequestsResponse: GetMateRequestsResponse? = null
    private var mHttpSourceGetMateRequestCountResponse: GetMateRequestCountResponse? = null

    private var mHttpSourceGetMateRequestsResponseCallFlag = false
    private var mHttpSourceGetMateRequestCountResponseCallFlag = false
    private var mHttpSourcePostMateRequestResponseCallFlag = false
    private var mHttpSourceAnswerMateRequestResponseCallFlag = false

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
        mHttpSourceGetMateRequestsResponse = null
        mHttpSourceGetMateRequestCountResponse = null

        mHttpSourceGetMateRequestsResponseCallFlag = false
        mHttpSourceGetMateRequestCountResponseCallFlag = false
        mHttpSourcePostMateRequestResponseCallFlag = false
        mHttpSourceAnswerMateRequestResponseCallFlag = false

        mHttpSourceGetMateRequestsCallFlag = false
        mHttpSourceGetMateRequestCountCallFlag = false
        mHttpSourcePostMateRequestCallFlag = false
        mHttpSourceAnswerMateRequestCallFlag = false
    }

    private fun initMateRequestDataRepository() {
        mErrorDataRepositoryMockContainer = ErrorDataRepositoryMockContainer()
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()
        mOkHttpClientMockContainer = OkHttpClientMockContainer()

        val httpMateRequestDataSourceMock = mockHttpMateRequestDataSource()

        mDataRepository = MateRequestDataRepository(
            mErrorDataRepository = mErrorDataRepositoryMockContainer.errorDataRepositoryMock,
            mTokenDataRepository = mTokenDataRepositoryMockContainer.tokenDataRepositoryMock,
            mUserDataRepository = mUserDataRepositoryMockContainer.userDataRepository,
            mHttpMateRequestDataSource = httpMateRequestDataSourceMock,
            mHttpClient = mOkHttpClientMockContainer.httpClient
        )
    }

    private fun mockHttpMateRequestDataSource(): HttpMateRequestDataSource {
        val getMateRequestsResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(getMateRequestsResponseMock.body()).thenAnswer {
            mHttpSourceGetMateRequestsResponseCallFlag = true
            mHttpSourceGetMateRequestsResponse
        }

        val getMateRequestCountResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(getMateRequestCountResponseMock.body()).thenAnswer {
            mHttpSourceGetMateRequestCountResponseCallFlag = true
            mHttpSourceGetMateRequestCountResponse
        }

        val postMateRequestResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(postMateRequestResponseMock.body()).thenAnswer {
            mHttpSourcePostMateRequestResponseCallFlag = true

            Unit
        }

        val answerMateRequestResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(answerMateRequestResponseMock.body()).thenAnswer {
            mHttpSourceAnswerMateRequestResponseCallFlag = true

            Unit
        }

        val getMateRequestsCallMock = Mockito.mock(Call::class.java)

        Mockito.`when`(getMateRequestsCallMock.execute()).thenAnswer {
            getMateRequestsResponseMock
        }

        val getMateRequestCountCallMock = Mockito.mock(Call::class.java)

        Mockito.`when`(getMateRequestCountCallMock.execute()).thenAnswer {
            getMateRequestCountResponseMock
        }

        val postMateRequestCallMock = Mockito.mock(Call::class.java)

        Mockito.`when`(postMateRequestCallMock.execute()).thenAnswer {
            postMateRequestResponseMock
        }

        val answerMateRequestCallMock = Mockito.mock(Call::class.java)

        Mockito.`when`(answerMateRequestCallMock.execute()).thenAnswer {
            answerMateRequestResponseMock
        }

        val httpMateRequestDataSourceMock = Mockito.mock(HttpMateRequestDataSource::class.java)

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

        mHttpSourceGetMateRequestsResponse = getMateRequestsResponse
        mUserDataRepositoryMockContainer.resolveUsers = resolveUsers

        val getMateRequestsResult = mDataRepository.getMateRequests(offset, count)
        val gottenDataMateRequests = getMateRequestsResult.requests

        Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)
        Assert.assertTrue(mHttpSourceGetMateRequestsCallFlag)
        Assert.assertTrue(mHttpSourceGetMateRequestsResponseCallFlag)
        Assert.assertTrue(mUserDataRepositoryMockContainer.resolveUsersCallFlag)
        AssertUtils.assertEqualContent(expectedDataMateRequests, gottenDataMateRequests)
    }

    @Test
    fun getMateRequestCountTest() = runTest {
        val getMateRequestCountResponse = GetMateRequestCountResponse(5)

        val expectedMateRequestCount = getMateRequestCountResponse.count

        mHttpSourceGetMateRequestCountResponse = getMateRequestCountResponse

        val getMateRequestCountResult = mDataRepository.getMateRequestCount()
        val gottenDataMateRequestCount = getMateRequestCountResult.count

        Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)
        Assert.assertTrue(mHttpSourceGetMateRequestCountCallFlag)
        Assert.assertTrue(mHttpSourceGetMateRequestCountResponseCallFlag)
        Assert.assertEquals(expectedMateRequestCount, gottenDataMateRequestCount)
    }

    @Test
    fun createMateRequestTest() = runTest {
        val userId = DEFAULT_USER.id

        mDataRepository.createMateRequest(userId)

        Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)
        Assert.assertTrue(mHttpSourcePostMateRequestCallFlag)
        Assert.assertTrue(mHttpSourcePostMateRequestResponseCallFlag)
    }

    @Test
    fun answerMateRequestTest() = runTest {
        val id = 0L
        val isAccepted = true

        mDataRepository.answerMateRequest(id, isAccepted)

        Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)
        Assert.assertTrue(mHttpSourceAnswerMateRequestCallFlag)
        Assert.assertTrue(mHttpSourceAnswerMateRequestResponseCallFlag)
    }
}