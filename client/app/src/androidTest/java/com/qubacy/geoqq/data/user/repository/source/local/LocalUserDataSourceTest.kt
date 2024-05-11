package com.qubacy.geoqq.data.user.repository.source.local

import com.qubacy.geoqq.data._common.repository.source_common.local.database.LocalDatabaseDataSourceTest
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao.LocalUserDatabaseDataSourceDao
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity
import org.junit.Assert
import org.junit.Test

class LocalUserDataSourceTest : LocalDatabaseDataSourceTest() {
    private lateinit var mLocalUserDataSource: LocalUserDatabaseDataSourceDao

    override fun setup() {
        super.setup()

        mLocalUserDataSource = mDatabase.userDao()
    }

    @Test
    fun insertUserThenGetItTest() {
        val expectedUser = generateUsers(count = 1).first()

        mLocalUserDataSource.insertUser(expectedUser)

        val gottenUser = mLocalUserDataSource.getUserById(expectedUser.id)

        Assert.assertEquals(expectedUser, gottenUser)
    }

    @Test
    fun getUsersByIdsTest() {
        val expectedUsers = generateUsers(count = 5)
        val expectedUserIds = expectedUsers.map { it.id }

        for (user in expectedUsers) mLocalUserDataSource.insertUser(user)

        val gottenUsers = mLocalUserDataSource.getUsersByIds(expectedUserIds)

        assertUserChunk(expectedUsers, gottenUsers)
    }

    @Test
    fun updateUserTest() {
        val userToUpdate = generateUsers(count = 1).first()
        val expectedUpdatedUser = userToUpdate.copy(username = "updated user")

        mLocalUserDataSource.insertUser(userToUpdate)
        mLocalUserDataSource.updateUser(expectedUpdatedUser)

        val gottenUpdatedUser = mLocalUserDataSource.getUserById(userToUpdate.id)

        Assert.assertEquals(expectedUpdatedUser, gottenUpdatedUser)
    }

    @Test
    fun deleteUserTest() {
        val userToDelete = generateUsers(count = 1).first()

        mLocalUserDataSource.insertUser(userToDelete)
        mLocalUserDataSource.deleteUser(userToDelete)

        val gottenUser = mLocalUserDataSource.getUserById(userToDelete.id)

        Assert.assertNull(gottenUser)
    }

    @Test
    fun saveUsersTest() {
        val initUsers = generateUsers(count = 3)
        val usersToSave = generateUsers(offset = 1, count = 3).toMutableList().apply {
            this[0] = this[0].copy(username = "updated user")
        }
        val expectedUsers = usersToSave.toMutableList().apply {
            add(0, initUsers.first())
        }
        val expectedUserIds = expectedUsers.map { it.id }

        for (user in initUsers) mLocalUserDataSource.insertUser(user)

        mLocalUserDataSource.saveUsers(usersToSave)

        val gottenUsers = mLocalUserDataSource.getUsersByIds(expectedUserIds)

        assertUserChunk(expectedUsers, gottenUsers)
    }

    private fun assertUserChunk(
        expectedUserChunk: List<UserEntity>,
        gottenUserChunk: List<UserEntity>?
    ) {
        Assert.assertNotNull(gottenUserChunk)
        Assert.assertEquals(expectedUserChunk.size, gottenUserChunk!!.size)

        for (expectedUser in expectedUserChunk)
            Assert.assertTrue(gottenUserChunk.contains(expectedUser))
    }

    private fun generateUsers(
        offset: Int = 0,
        count: Int
    ): List<UserEntity> {
        return IntRange(offset, offset + count - 1).map {
            val id = it.toLong()

            UserEntity(id, "test user $id", "description $id",
                0, 0, 0)
        }
    }
}