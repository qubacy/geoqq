package com.qubacy.geoqq.data.user.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data.error.repository._test.mock.ErrorDataRepositoryMockContainer
import com.qubacy.geoqq.data.image.repository._test.mock.ImageDataRepositoryMockContainer
import com.qubacy.geoqq.data.token.repository._test.mock.TokenDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.model.toDataUser
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsDataResult
import com.qubacy.geoqq.data.user.repository.source.http.HttpUserDataSource
import com.qubacy.geoqq.data.user.repository.source.http.response.GetUserResponse
import com.qubacy.geoqq.data.user.repository.source.http.response.GetUsersResponse
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito
import retrofit2.Call
import retrofit2.Response

class UserDataRepositoryTest : DataRepositoryTest<UserDataRepository>() {
    @get:Rule
    val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mErrorDataRepositoryMockContainer: ErrorDataRepositoryMockContainer
    private lateinit var mTokenDataRepositoryMockContainer: TokenDataRepositoryMockContainer
    private lateinit var mImageDataRepositoryMockContainer: ImageDataRepositoryMockContainer

    private var mLocalSourceGetUsersByIds: List<UserEntity>? = null
    private var mLocalSourceGetUserById: UserEntity? = null

    private var mLocalSourceGetUsersByIdsCallFlag = false
    private var mLocalSourceGetUserByIdCallFlag = false
    private var mLocalSourceUpdateUserCallFlag = false
    private var mLocalSourceInsertUserCallFlag = false
    private var mLocalSourceDeleteUserCallFlag = false
    private var mLocalSourceSaveUsersCallFlag = false

    private var mHttpSourceGetUsersResponse: GetUsersResponse? = null

    private var mHttpSourceGetUsersResponseCallFlag = false
    private var mHttpSourceGetUsersCallFlag = false

    @Before
    fun setup() {
        initUserDataRepository()
    }

    @After
    fun clear() {
        mLocalSourceGetUsersByIds = null
        mLocalSourceGetUserById = null

        mLocalSourceGetUsersByIdsCallFlag = false
        mLocalSourceGetUserByIdCallFlag = false
        mLocalSourceUpdateUserCallFlag = false
        mLocalSourceInsertUserCallFlag = false
        mLocalSourceDeleteUserCallFlag = false
        mLocalSourceSaveUsersCallFlag = false

        mHttpSourceGetUsersResponse = null

        mHttpSourceGetUsersResponseCallFlag = false
        mHttpSourceGetUsersCallFlag = false
    }

    private fun initUserDataRepository() {
        mErrorDataRepositoryMockContainer = ErrorDataRepositoryMockContainer()
        mTokenDataRepositoryMockContainer = TokenDataRepositoryMockContainer()
        mImageDataRepositoryMockContainer = ImageDataRepositoryMockContainer()

        val localUserDataSourceMock = mockLocalUserDataSource()
        val httpUserDataSourceMock = mockHttpUserDataSource()

        mDataRepository = UserDataRepository(
            mErrorDataRepository = mErrorDataRepositoryMockContainer.errorDataRepositoryMock,
            mTokenDataRepository = mTokenDataRepositoryMockContainer.tokenDataRepositoryMock,
            mImageDataRepository = mImageDataRepositoryMockContainer.imageDataRepositoryMock,
            mLocalUserDataSource = localUserDataSourceMock,
            mHttpUserDataSource = httpUserDataSourceMock
        )
    }

    private fun mockLocalUserDataSource(): LocalUserDataSource {
        val localUserDataSource = Mockito.mock(LocalUserDataSource::class.java)

        Mockito.`when`(localUserDataSource.getUsersByIds(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceGetUsersByIdsCallFlag = true
            mLocalSourceGetUsersByIds
        }
        Mockito.`when`(localUserDataSource.getUserById(Mockito.anyLong())).thenAnswer {
            mLocalSourceGetUserByIdCallFlag = true
            mLocalSourceGetUserById
        }
        Mockito.`when`(localUserDataSource.updateUser(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceUpdateUserCallFlag = true

            Unit
        }
        Mockito.`when`(localUserDataSource.insertUser(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceInsertUserCallFlag = true

            Unit
        }
        Mockito.`when`(localUserDataSource.deleteUser(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceDeleteUserCallFlag = true

            Unit
        }
        Mockito.`when`(localUserDataSource.saveUsers(AnyMockUtil.anyObject())).thenAnswer {
            mLocalSourceSaveUsersCallFlag = true

            Unit
        }

        return localUserDataSource
    }

    private fun mockHttpUserDataSource(): HttpUserDataSource {
        val getUsersResponseMock = Mockito.mock(Response::class.java)

        Mockito.`when`(getUsersResponseMock.body()).thenAnswer {
            mHttpSourceGetUsersResponseCallFlag = true
            mHttpSourceGetUsersResponse
        }

        val getUsersCallMock = Mockito.mock(Call::class.java)

        Mockito.`when`(getUsersCallMock.execute()).thenAnswer {
            getUsersResponseMock
        }

        val httpUserDataSource = Mockito.mock(HttpUserDataSource::class.java)

        Mockito.`when`(httpUserDataSource.getUsers(AnyMockUtil.anyObject())).thenAnswer {
            mHttpSourceGetUsersCallFlag = true
            getUsersCallMock
        }

        return httpUserDataSource
    }

    @Test
    fun getUsersByIdsTest() = runTest {
        val localUsers = listOf(
            UserEntity(0, "local user", String(), 0L, 0, 0)
        )
        val httpUsers = GetUsersResponse(listOf(
            GetUserResponse(
                0, "http user", "desc", 0L, false, false
            )
        ))
        val avatars = listOf(
            ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE
                .copy(id = localUsers.first().avatarId)
        )
        val userIds = httpUsers.users.map { it.id }

        mLocalSourceGetUsersByIds = localUsers
        mHttpSourceGetUsersResponse = httpUsers
        mImageDataRepositoryMockContainer.getImagesByIds = avatars

        val expectedLocalDataUsers = localUsers.map {
            it.toDataUser(ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE)
        }
        val expectedHttpDataUsers = httpUsers.users.map {
            it.toDataUser(ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE)
        }

        mDataRepository.resultFlow.test {
            val gottenGetUsersByIdsResult = mDataRepository.getUsersByIds(userIds).await()

            Assert.assertTrue(mLocalSourceGetUsersByIdsCallFlag)

            val gottenLocalDataUsers = gottenGetUsersByIdsResult.users

            AssertUtils.assertEqualContent(expectedLocalDataUsers, gottenLocalDataUsers)

            Assert.assertTrue(mTokenDataRepositoryMockContainer.getTokensCallFlag)

            val gottenHttpResult = awaitItem()

            Assert.assertTrue(mHttpSourceGetUsersCallFlag)
            Assert.assertTrue(mHttpSourceGetUsersResponseCallFlag)
            Assert.assertEquals(GetUsersByIdsDataResult::class, gottenHttpResult::class)

            val gottenHttpDataUsers = (gottenHttpResult as GetUsersByIdsDataResult).users

            AssertUtils.assertEqualContent(expectedHttpDataUsers, gottenHttpDataUsers)
        }
    }
}