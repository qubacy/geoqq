package com.qubacy.geoqq.data.user.repository

import com.qubacy.geoqq.data.common.repository.network.NetworkTestContext
import com.qubacy.geoqq.data.user.repository.result.GetUsersByIdsResult
import com.qubacy.geoqq.data.user.repository.source.local.LocalUserDataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity
import com.qubacy.geoqq.data.user.repository.source.network.NetworkUserDataSource
import com.qubacy.geoqq.data.user.repository.source.network.model.response.GetUsersResponse
import com.qubacy.geoqq.data.user.repository.source.network.model.response.NetworkUserModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.lang.StringBuilder

class UserDataRepositoryTest() {
    private lateinit var mUserDataRepository: UserDataRepository

    private fun generateUsersNetworkResponse(usersFromNetwork: List<NetworkUserModel>): String {
        val responseStringBuilder = StringBuilder("{\"users\": [")

        for (index in usersFromNetwork.indices) {
            responseStringBuilder.append(
                "{\"id\":${usersFromNetwork[index].id}," +
                "\"username\":\"${usersFromNetwork[index].username}\"," +
                "\"description\":\"${usersFromNetwork[index].description}\"," +
                "\"avatar-id\":${usersFromNetwork[index].avatarId}," +
                "\"is-mate\":${usersFromNetwork[index].isMate}}"
            )
            responseStringBuilder.append(if (index == usersFromNetwork.size - 1) "" else ",")
        }

        return responseStringBuilder.append("]}").toString()
    }

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
        val userEntities = listOf(UserEntity(
            0, "test", "test desc", 0, 0))

        initDataRepository(localUserEntity = userEntities.first())

        runBlocking {
            val getUsersByIdsResult = mUserDataRepository
                .getUsersByIds(userEntities.map { it.id }, String())

            Assert.assertEquals(GetUsersByIdsResult::class, getUsersByIdsResult::class)

            val gottenUsers = (getUsersByIdsResult as GetUsersByIdsResult).users

            Assert.assertEquals(userEntities.first().id, gottenUsers.first().id)
        }
    }

    @Test
    fun getUserByIdFromNetworkTest() {
        val userResponseObj = GetUsersResponse(
            listOf(
                NetworkUserModel(0, "test", "desc", 0, false)
            )
        )
        val responseString = generateUsersNetworkResponse(userResponseObj.users)

        initDataRepository(code = 200, responseString = responseString)

        runBlocking {
            val getUsersByIdsResult = mUserDataRepository
                .getUsersByIds(userResponseObj.users.map { it.id }, String())

            Assert.assertEquals(GetUsersByIdsResult::class, getUsersByIdsResult::class)

            val gottenUsers = (getUsersByIdsResult as GetUsersByIdsResult).users

            Assert.assertEquals(userResponseObj.users.first().id, gottenUsers.first().id)
        }
    }
}