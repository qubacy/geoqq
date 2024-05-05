package com.qubacy.geoqq.data.user.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.auth0.android.jwt.Claim
import com.qubacy.geoqq._common._test.rule.dispatcher.MainDispatcherRule
import com.qubacy.geoqq._common._test.util.assertion.AssertUtils
import com.qubacy.geoqq._common._test.util.mock.AnyMockUtil
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq._common.exception.error.ErrorAppException
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository.DataRepositoryTest
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._test.mock.ErrorDataSourceMockContainer
import com.qubacy.geoqq.data._common.repository._common.source.local.datastore.token.LocalTokenDataStoreDataSource
import com.qubacy.geoqq.data.image.repository._test.mock.ImageDataRepositoryMockContainer
import com.qubacy.geoqq.data.user.model.toDataUser
import com.qubacy.geoqq.data.user.repository.source.http.HttpUserDataSource
import com.qubacy.geoqq.data.user.repository.source.http.api.response.GetUserResponse
import com.qubacy.geoqq.data.user.repository.source.http.api.response.GetUsersResponse
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.mockito.Mockito
import java.util.Date

class UserDataRepositoryTest : DataRepositoryTest<UserDataRepository>() {
    companion object {
        const val DEFAULT_LOCAL_USER_ID = 0L

        val DEFAULT_ACCESS_TOKEN_USER_ID_CLAIM = object : Claim {
            override fun asBoolean(): Boolean? = null
            override fun asInt(): Int? = null
            override fun asLong(): Long = DEFAULT_LOCAL_USER_ID
            override fun asDouble(): Double? = null
            override fun asString(): String? = null
            override fun asDate(): Date? = null
            override fun <T : Any?> asArray(tClazz: Class<T>?): Array<T> = null as Array<T>
            override fun <T : Any?> asList(tClazz: Class<T>?): MutableList<T> = mutableListOf()
            override fun <T : Any?> asObject(tClazz: Class<T>?): T? = null
        }

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
    }

    @get:Rule
    val rule = RuleChain
        .outerRule(InstantTaskExecutorRule())
        .around(MainDispatcherRule())

    private lateinit var mErrorDataSourceMockContainer: ErrorDataSourceMockContainer
    private lateinit var mImageDataRepositoryMockContainer: ImageDataRepositoryMockContainer

    private var mLocalSourceGetUsersByIds: List<UserEntity>? = null
    private var mLocalSourceGetUserById: UserEntity? = null

    private var mLocalSourceGetUsersByIdsCallFlag = false
    private var mLocalSourceGetUserByIdCallFlag = false
    private var mLocalSourceUpdateUserCallFlag = false
    private var mLocalSourceInsertUserCallFlag = false
    private var mLocalSourceDeleteUserCallFlag = false
    private var mLocalSourceSaveUsersCallFlag = false

    private var mLocalSourceGetAccessToken: String? = null

    private var mLocalSourceGetAccessTokenCallFlag = false

    private var mHttpSourceGetUsersResponse: GetUsersResponse? = null

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

        mLocalSourceGetAccessToken = null

        mLocalSourceGetAccessTokenCallFlag = false

        mHttpSourceGetUsersResponse = null

        mHttpSourceGetUsersCallFlag = false
    }

    private fun initUserDataRepository() {
        mErrorDataSourceMockContainer = ErrorDataSourceMockContainer()
        mImageDataRepositoryMockContainer = ImageDataRepositoryMockContainer()

        val localUserDataSourceMock = mockLocalUserDataSource()
        val localTokenDataStoreDataSourceMock = mockTokenDataStoreDataSource()
        val httpUserDataSourceMock = mockHttpUserDataSource()

        mDataRepository = UserDataRepository(
            mErrorSource = mErrorDataSourceMockContainer.errorDataSourceMock,
            mImageDataRepository = mImageDataRepositoryMockContainer.imageDataRepositoryMock,
            mLocalUserDataSource = localUserDataSourceMock,
            mLocalTokenDataStoreDataSource = localTokenDataStoreDataSourceMock,
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

    private fun mockTokenDataStoreDataSource(): LocalTokenDataStoreDataSource {
        val tokenDataStoreDataSourceMock = Mockito.mock(LocalTokenDataStoreDataSource::class.java)

        runTest {
            Mockito.`when`(tokenDataStoreDataSourceMock.getAccessToken()).thenAnswer {
                mLocalSourceGetAccessTokenCallFlag = true
                mLocalSourceGetAccessToken
            }
        }

        return tokenDataStoreDataSourceMock
    }

    private fun mockHttpUserDataSource(): HttpUserDataSource {
        val httpUserDataSource = Mockito.mock(HttpUserDataSource::class.java)

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
        val accessTokenUserIdClaim = DEFAULT_ACCESS_TOKEN_USER_ID_CLAIM
        val getAccessTokenPayload = mapOf(
            UserDataRepository.ACCESS_TOKEN_USER_ID_PAYLOAD_PROP_NAME to accessTokenUserIdClaim
        )

        mLocalSourceGetUsersByIds = localUsers
        mImageDataRepositoryMockContainer.getImagesByIds = avatars
        mHttpSourceGetUsersResponse = httpUsers
        mLocalSourceGetAccessToken =

        val expectedLocalUser = localUser

        val gottenLocalUser = mDataRepository.resolveLocalUser()

        Assert.assertTrue(mLocalSourceGetUsersByIdsCallFlag)
        Assert.assertTrue(mLocalSourceGetAccessTokenCallFlag)
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
        val accessTokenUserIdClaim = DEFAULT_ACCESS_TOKEN_USER_ID_CLAIM
        val getAccessTokenPayload = mapOf(
            UserDataRepository.ACCESS_TOKEN_USER_ID_PAYLOAD_PROP_NAME to accessTokenUserIdClaim
        )

        mLocalSourceGetUsersByIds = localUsers
        mImageDataRepositoryMockContainer.getImagesByIds = avatars
        mHttpSourceGetUsersResponse = httpUsers
        mLocalSourceGetAccessToken =

        val expectedResolvedUsers = localUsers.map {
            it.toDataUser(ImageDataRepositoryMockContainer.DEFAULT_DATA_IMAGE)
        }.associateBy { it.id }

        val resolveUsersResult = mDataRepository.resolveUsersWithLocalUser(userIds)
        val gottenResolvedUsers = resolveUsersResult.await().userIdUserMap

        Assert.assertTrue(mLocalSourceGetUsersByIdsCallFlag)
        Assert.assertTrue(mLocalSourceGetAccessTokenCallFlag)
        Assert.assertTrue(mHttpSourceGetUsersCallFlag)
        Assert.assertTrue(mImageDataRepositoryMockContainer.getImagesByIdsCallFlag)

        AssertUtils.assertEqualMaps(expectedResolvedUsers, gottenResolvedUsers)
    }
}