package com.qubacy.geoqq.data.mate.request.repository.impl

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data.mate.request.model.toDataMateRequest
import com.qubacy.geoqq.data.mate.request.repository._common._test.context.MateRequestDataRepositoryTestContext
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.RemoteMateRequestHttpRestDataSource
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response.GetMateRequestCountResponse
import com.qubacy.geoqq.data.mate.request.repository._common.source.remote.http.rest._common.api.response.GetMateRequestsResponse
import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext
import com.qubacy.geoqq.data.user.repository._common._test.mock.UserDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.repository._common.result.resolve.ResolveUsersDataResult
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class MateRequestDataRepositoryImplTest : DataRepositoryTest<MateRequestDataRepositoryImpl>() {
    companion object {
        val DEFAULT_DATA_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER
        val DEFAULT_USER_ID_USER_MAP = UserDataRepositoryTestContext.DEFAULT_USER_ID_USER_MAP
        val DEFAULT_GET_MATE_REQUEST_RESPONSE = MateRequestDataRepositoryTestContext
            .DEFAULT_GET_MATE_REQUEST_RESPONSE
    }

    @get:Rule
    val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

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

        mDataRepository = MateRequestDataRepositoryImpl(
            mErrorSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            mUserDataRepository = mUserDataRepositoryMockContainer.userDataRepository,
            mRemoteMateRequestHttpRestDataSource = httpMateRequestDataSourceMock
        )
    }

    private fun mockHttpMateRequestDataSource(): RemoteMateRequestHttpRestDataSource {
        val httpMateRequestDataSourceMock = Mockito.mock(RemoteMateRequestHttpRestDataSource::class.java)

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
            Mockito.anyLong()
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
            DEFAULT_GET_MATE_REQUEST_RESPONSE
        ))

        val userIdUserMap = DEFAULT_USER_ID_USER_MAP

        val remoteUser = userIdUserMap[DEFAULT_DATA_USER.id]!!

        val expectedRemoteDataMateRequests = getMateRequestsResponse.requests.map {
            it.toDataMateRequest(remoteUser)
        }

        mHttpSourceGetMateRequestsResponse = getMateRequestsResponse
        mUserDataRepositoryMockContainer.resolveUsersResult =
            ResolveUsersDataResult(true, userIdUserMap)

        val getMateRequestsResult = mDataRepository.getMateRequests(offset, count)

        val gottenRemoteDataMateRequests = getMateRequestsResult.awaitUntilVersion(0).requests

        Assert.assertTrue(mHttpSourceGetMateRequestsCallFlag)
        Assert.assertTrue(mUserDataRepositoryMockContainer.resolveUsersCallFlag)

        AssertUtils.assertEqualContent(expectedRemoteDataMateRequests, gottenRemoteDataMateRequests)
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
        val userId = DEFAULT_DATA_USER.id

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