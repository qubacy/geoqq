package com.qubacy.geoqq.data.mate.request.repository

import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data.mate.request.model.toDataMateRequest
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.api.response.GetMateRequestsResponse
import com.qubacy.geoqq.data.mate.request.repository.source.http.HttpMateRequestDataSource
import com.qubacy.geoqq.data.user.repository._test.mock.UserDataRepositoryMockContainer
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class MateRequestDataRepositoryTest : DataRepositoryTest<MateRequestDataRepository>() {
    companion object {
        val DEFAULT_USER = UserDataRepositoryMockContainer.DEFAULT_DATA_USER
        val DEFAULT_MATE_REQUEST = GetMateRequestResponse(0, DEFAULT_USER.id)
    }

    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mUserDataRepositoryMockContainer: UserDataRepositoryMockContainer

    private var mHttpSourceGetMateRequestsResponse: GetMateRequestsResponse? = null
    private var mHttpSourceGetMateRequestCountResponse: GetMateRequestCountResponse? = null

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

        mHttpSourceGetMateRequestsCallFlag = false
        mHttpSourceGetMateRequestCountCallFlag = false
        mHttpSourcePostMateRequestCallFlag = false
        mHttpSourceAnswerMateRequestCallFlag = false
    }

    private fun initMateRequestDataRepository() {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mUserDataRepositoryMockContainer = UserDataRepositoryMockContainer()

        val httpMateRequestDataSourceMock = mockHttpMateRequestDataSource()

        mDataRepository = MateRequestDataRepository(
            mErrorSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            mUserDataRepository = mUserDataRepositoryMockContainer.userDataRepository,
            mHttpMateRequestDataSource = httpMateRequestDataSourceMock
        )
    }

    private fun mockHttpMateRequestDataSource(): HttpMateRequestDataSource {
        val httpMateRequestDataSourceMock = Mockito.mock(HttpMateRequestDataSource::class.java)

        Mockito.`when`(httpMateRequestDataSourceMock.getMateRequests(
            Mockito.anyInt(),
            Mockito.anyInt()
        )).thenAnswer {
            mHttpSourceGetMateRequestsCallFlag = true
            mHttpSourceGetMateRequestsResponse
        }
        Mockito.`when`(httpMateRequestDataSourceMock.getMateRequestCount()).thenAnswer {
            mHttpSourceGetMateRequestCountCallFlag = true
            mHttpSourceGetMateRequestCountResponse
        }
        Mockito.`when`(httpMateRequestDataSourceMock.postMateRequest(
            AnyMockUtil.anyObject()
        )).thenAnswer {
            mHttpSourcePostMateRequestCallFlag = true

            Unit
        }
        Mockito.`when`(httpMateRequestDataSourceMock.answerMateRequest(
            Mockito.anyLong(),
            Mockito.anyBoolean()
        )).thenAnswer {
            mHttpSourceAnswerMateRequestCallFlag = true

            Unit
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
        val gottenDataMateRequests = getMateRequestsResult.await().requests

        Assert.assertTrue(mHttpSourceGetMateRequestsCallFlag)
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

        Assert.assertTrue(mHttpSourceGetMateRequestCountCallFlag)
        Assert.assertEquals(expectedMateRequestCount, gottenDataMateRequestCount)
    }

    @Test
    fun createMateRequestTest() = runTest {
        val userId = DEFAULT_USER.id

        mDataRepository.createMateRequest(userId)

        Assert.assertTrue(mHttpSourcePostMateRequestCallFlag)
    }

    @Test
    fun answerMateRequestTest() = runTest {
        val id = 0L
        val isAccepted = true

        mDataRepository.answerMateRequest(id, isAccepted)

        Assert.assertTrue(mHttpSourceAnswerMateRequestCallFlag)
    }
}