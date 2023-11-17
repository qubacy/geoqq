package com.qubacy.geoqq.data.user.repository

import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.user.repository.result.GetUserByIdResult
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity
import com.qubacy.geoqq.data.user.repository.source.network.NetworkUserDataSource
import com.qubacy.geoqq.data.user.repository.source.network.response.GetUsersResponse
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class UserDataRepositoryTest() {
    private lateinit var mUserDataRepository: UserDataRepository

    private fun initDataRepository(
        localUserEntity: UserEntity? = null,
        code: Int = 200,
        responseString: String = String()
    ) {
        val localUserDataSource = Mockito.mock(LocalUserDataSource::class.java)

        Mockito.`when`(localUserDataSource.getUserById(Mockito.anyLong()))
            .thenReturn(localUserEntity)

        val networkUserDataSource = NetworkTestContext.generateTestRetrofit(
            NetworkTestContext.generateDefaultTestInterceptor(code, responseString)
        ).create(NetworkUserDataSource::class.java)

        mUserDataRepository = UserDataRepository(localUserDataSource, networkUserDataSource)
    }

    @Before
    fun setup() {
        initDataRepository()
    }

    @Test
    fun getUserByIdFromLocalDatabaseTest() {
        val userEntity = UserEntity(
            0, "test", "test desc", 0, 0)

        initDataRepository(localUserEntity = userEntity)

        runBlocking {
            val getUserByIdResult = mUserDataRepository.getUserById(userEntity.id, String())

            Assert.assertEquals(GetUserByIdResult::class, getUserByIdResult::class)

            val gottenUser = (getUserByIdResult as GetUserByIdResult).user

            Assert.assertEquals(userEntity.id, gottenUser.id)
        }
    }

    @Test
    fun getUserByIdFromNetworkTest() {
        val userResponseObj = GetUsersResponse(
            0, "test", "desc", 0, false)
        val responseString = "{\"id\":${userResponseObj.id}," +
                "\"username\":\"${userResponseObj.username}\"," +
                "\"description\":\"${userResponseObj.description}\"," +
                "\"avatar-id\":${userResponseObj.avatarId}," +
                "\"is-mate\":${userResponseObj.isMate}}"

        initDataRepository(code = 200, responseString = responseString)

        runBlocking {
            val getUserByIdResult = mUserDataRepository.getUserById(userResponseObj.id, String())

            Assert.assertEquals(GetUserByIdResult::class, getUserByIdResult::class)

            val gottenUser = (getUserByIdResult as GetUserByIdResult).user

            Assert.assertEquals(userResponseObj.id, gottenUser.id)
        }
    }
}