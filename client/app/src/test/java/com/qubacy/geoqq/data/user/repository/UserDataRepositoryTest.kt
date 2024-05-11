package com.qubacy.geoqq.data.user.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common._test.util.mock.Base64MockUtil
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token._test.mock.LocalTokenDataStoreDataSourceMockContainer
import com.qubacy.geoqq.data.image.repository._test.mock.ImageDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.model.toDataUser
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest.impl.RemoteUserHttpRestDataSourceImpl
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.response.GetUserResponse
import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.response.GetUsersResponse
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao.LocalUserDatabaseDataSourceDao
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity
import com.qubacy.geoqq.data.user.repository.impl.UserDataRepositoryImpl
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito

class UserDataRepositoryTest : DataRepositoryTest<UserDataRepositoryImpl>() {
    companion object {
        const val DEFAULT_LOCAL_USER_ID = 0L

        const val DEFAULT_ACCESS_TOKEN = LocalTokenDataStoreDataSourceMockContainer.VALID_TOKEN

        val DEFAULT_USER_ENTITY = UserEntity(
            DEFAULT_LOCAL_USER_ID,
            "local user", String(),
            0L, 0, 0
        )
        val DEFAULT_GET_USER_RESPONSE = GetUserResponse(
            DEFAULT_LOCAL_USER_ID, "http user", "desc",
            0L, false, false
        )
        val DEFAULT_AVATAR = ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE
            .copy(id = DEFAULT_USER_ENTITY.avatarId)

        const val DEFAULT_TOKEN_WITH_USER_ID_0 = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJ1c2VyLWlkIjowfQ.NLUO7-jk654bGjYSRVfTlWPVZNgm0hxYlYo_FTMQ1Qg"
    }

    @get:Rule
    val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mImageDataRepositoryMockContainer: ImageDataRepositoryMockContainer
    private lateinit var mLocalTokenDataStoreDataSourceMockContainer:
        LocalTokenDataStoreDataSourceMockContainer

    private var mLocalSourceGetUsersByIds: List<UserEntity>? = null
    private var mLocalSourceGetUserById: UserEntity? = null

    private var mLocalSourceGetUsersByIdsCallFlag = false
    private var mLocalSourceGetUserByIdCallFlag = false
    private var mLocalSourceUpdateUserCallFlag = false
    private var mLocalSourceInsertUserCallFlag = false
    private var mLocalSourceDeleteUserCallFlag = false
    private var mLocalSourceSaveUsersCallFlag = false

    private var mHttpSourceGetUsersResponse: GetUsersResponse? = null

    private var mHttpSourceGetUsersCallFlag = false

    @Before
    fun setup() {
        Base64MockUtil.mockBase64()
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

        mHttpSourceGetUsersCallFlag = false
    }

    private fun initUserDataRepository() {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mImageDataRepositoryMockContainer = ImageDataRepositoryMockContainer()
        mLocalTokenDataStoreDataSourceMockContainer = LocalTokenDataStoreDataSourceMockContainer()

        val localUserDataSourceMock = mockLocalUserDataSource()
        val httpUserDataSourceMock = mockHttpUserDataSource()

        mDataRepository = UserDataRepositoryImpl(
            mErrorSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            mImageDataRepository = mImageDataRepositoryMockContainer.imageDataRepositoryMock,
            mLocalUserDatabaseDataSource = localUserDataSourceMock,
            mLocalTokenDataStoreDataSource =
                mLocalTokenDataStoreDataSourceMockContainer.localTokenDataStoreDataSourceMock,
            mRemoteUserHttpRestDataSource = httpUserDataSourceMock
        )
    }

    private fun mockLocalUserDataSource(): LocalUserDatabaseDataSourceDao {
        val localUserDataSource = Mockito.mock(LocalUserDatabaseDataSourceDao::class.java)

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

    private fun mockHttpUserDataSource(): RemoteUserHttpRestDataSourceImpl {
        val httpUserDataSource = Mockito.mock(RemoteUserHttpRestDataSourceImpl::class.java)

        Mockito.`when`(httpUserDataSource.getUsers(AnyMockUtil.anyObject())).thenAnswer {
            mHttpSourceGetUsersCallFlag = true

            if (mErrorDataSourceMockContainer.getError != null)
                throw ErrorAppException(mErrorDataSourceMockContainer.getError!!)

            mHttpSourceGetUsersResponse
        }

        return httpUserDataSource
    }

    @Test
    fun getUsersByIdsTest() = runTest {
        val localUsers = listOf(DEFAULT_USER_ENTITY)
        val httpUsers = GetUsersResponse(listOf(DEFAULT_GET_USER_RESPONSE))
        val avatars = listOf(DEFAULT_AVATAR)
        val userIds = httpUsers.users.map { it.id }

        mLocalSourceGetUsersByIds = localUsers
        mHttpSourceGetUsersResponse = httpUsers
        mImageDataRepositoryMockContainer.getImagesByIds = avatars

        val expectedLocalDataUsers = localUsers.map {
            it.toDataUser(ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE)
        }
        val expectedRemoteDataUsers = httpUsers.users.map {
            it.toDataUser(ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE)
        }

        val getUsersResult = mDataRepository.getUsersByIds(userIds)

        val gottenLocalGetUsersResult = getUsersResult.awaitUntilVersion(0)

        Assert.assertTrue(mLocalSourceGetUsersByIdsCallFlag)

        val gottenLocalDataUsers = gottenLocalGetUsersResult.users

        AssertUtils.assertEqualContent(expectedLocalDataUsers, gottenLocalDataUsers)

        Assert.assertTrue(mHttpSourceGetUsersCallFlag)

        val gottenRemoteGetUsersResult = getUsersResult.awaitUntilVersion(1)
        val gottenRemoteDataUsers = gottenRemoteGetUsersResult.users

        AssertUtils.assertEqualContent(expectedRemoteDataUsers, gottenRemoteDataUsers)
    }

    @Test
    fun getUsersByIdsThrowsExceptionOnNoUsersGottenTest() = runTest {
        val httpUsers = GetUsersResponse(listOf())
        val userIds = listOf(DEFAULT_LOCAL_USER_ID)
        val expectedError = Error(0, String(), false)

        mLocalSourceGetUsersByIds = listOf()
        mHttpSourceGetUsersResponse = httpUsers
        mErrorDataSourceMockContainer.getError = expectedError

        val exception = Assert.assertThrows(ErrorAppException::class.java) {
            runBlocking {
                mDataRepository.getUsersByIds(userIds)
            }
        }

        Assert.assertTrue(mHttpSourceGetUsersCallFlag)
        Assert.assertEquals(ErrorAppException::class, exception::class)

        val gottenError = (exception as ErrorAppException).error

        Assert.assertEquals(expectedError, gottenError)
    }

    @Test
    fun resolveUsersTest() = runTest {
        val localUsers = listOf(DEFAULT_USER_ENTITY)
        val httpUsers = GetUsersResponse(listOf(DEFAULT_GET_USER_RESPONSE))
        val avatars = listOf(DEFAULT_AVATAR)
        val userIds = localUsers.map { it.id }

        mLocalSourceGetUsersByIds = localUsers
        mImageDataRepositoryMockContainer.getImagesByIds = avatars
        mHttpSourceGetUsersResponse = httpUsers

        val expectedResolvedUsers = localUsers.map {
            it.toDataUser(ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE)
        }.associateBy { it.id }

        val gottenResolveUsersResult = mDataRepository.resolveUsers(userIds).await()
        val gottenResolvedUsers = gottenResolveUsersResult.userIdUserMap

        Assert.assertTrue(mLocalSourceGetUsersByIdsCallFlag)
        Assert.assertTrue(mImageDataRepositoryMockContainer.getImagesByIdsCallFlag)
        Assert.assertTrue(mHttpSourceGetUsersCallFlag)
        AssertUtils.assertEqualMaps(expectedResolvedUsers, gottenResolvedUsers)
    }

    @Test
    fun resolveLocalUserTest() = runTest {
        val localUsers = listOf(DEFAULT_USER_ENTITY)
        val httpUsers = GetUsersResponse(listOf(DEFAULT_GET_USER_RESPONSE))
        val avatars = listOf(DEFAULT_AVATAR)
        val localUser = localUsers.first()
            .toDataUser(ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE)

        mLocalSourceGetUsersByIds = localUsers
        mImageDataRepositoryMockContainer.getImagesByIds = avatars
        mHttpSourceGetUsersResponse = httpUsers
        mLocalTokenDataStoreDataSourceMockContainer.getAccessToken = DEFAULT_ACCESS_TOKEN

        val expectedLocalUser = localUser

        val gottenLocalUser = mDataRepository.resolveLocalUser()

        Assert.assertTrue(mLocalSourceGetUsersByIdsCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.getAccessTokenCallFlag)
        Assert.assertTrue(mImageDataRepositoryMockContainer.getImagesByIdsCallFlag)
        Assert.assertTrue(mHttpSourceGetUsersCallFlag)
        Assert.assertEquals(expectedLocalUser, gottenLocalUser)
    }

    @Test
    fun resolveUsersWithLocalUserTest() = runTest {
        val localUsers = listOf(DEFAULT_USER_ENTITY)
        val httpUsers = GetUsersResponse(listOf(DEFAULT_GET_USER_RESPONSE))
        val avatars = listOf(DEFAULT_AVATAR)
        val userIds = localUsers.map { it.id }

        mLocalSourceGetUsersByIds = localUsers
        mImageDataRepositoryMockContainer.getImagesByIds = avatars
        mHttpSourceGetUsersResponse = httpUsers
        mLocalTokenDataStoreDataSourceMockContainer.getAccessToken = DEFAULT_ACCESS_TOKEN

        val expectedResolvedUsers = localUsers.map {
            it.toDataUser(ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE)
        }.associateBy { it.id }

        val resolveUsersResult = mDataRepository.resolveUsersWithLocalUser(userIds)
        val gottenResolvedUsers = resolveUsersResult.await().userIdUserMap

        Assert.assertTrue(mLocalSourceGetUsersByIdsCallFlag)
        Assert.assertTrue(mLocalTokenDataStoreDataSourceMockContainer.getAccessTokenCallFlag)
        Assert.assertTrue(mHttpSourceGetUsersCallFlag)
        Assert.assertTrue(mImageDataRepositoryMockContainer.getImagesByIdsCallFlag)

        AssertUtils.assertEqualMaps(expectedResolvedUsers, gottenResolvedUsers)
    }

    @Test
    fun getLocalUserIdTest() {
        val accessToken = DEFAULT_TOKEN_WITH_USER_ID_0

        val expectedLocalUserId = 0L

        mLocalTokenDataStoreDataSourceMockContainer.getAccessToken = accessToken

        val gottenLocalUserId = mDataRepository.getLocalUserId()

        Assert.assertEquals(expectedLocalUserId, gottenLocalUserId)
    }
}