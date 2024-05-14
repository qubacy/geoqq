package com.qubacy.geoqq.data.user.repository.source.local.database._common.dao

import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.LocalDatabaseDataSourceTest
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao.LocalUserDatabaseDataSourceDao
import com.qubacy.geoqq.data.user.repository.source.local.database._common._test.context.LocalUserDatabaseDataSourceTestContext
import org.junit.Assert
import org.junit.Test

class LocalUserDatabaseDataSourceDaoTest : LocalDatabaseDataSourceTest() {
    companion object {
        val DEFAULT_USER_ENTITY = LocalUserDatabaseDataSourceTestContext.DEFAULT_USER_ENTITY
    }

    private lateinit var mLocalUserDataSource: LocalUserDatabaseDataSourceDao

    override fun setup() {
        super.setup()

        mLocalUserDataSource = mDatabase.userDao()
    }

    @Test
    fun insertUserThenGetItTest() {
        val expectedUser = DEFAULT_USER_ENTITY

        mLocalUserDataSource.insertUser(expectedUser)

        val gottenUser = mLocalUserDataSource.getUserById(expectedUser.id)

        Assert.assertEquals(expectedUser, gottenUser)
    }

    @Test
    fun getUserByIdTest() {
        val expectedUser = DEFAULT_USER_ENTITY

        mLocalUserDataSource.insertUser(expectedUser)

        val gottenUser = mLocalUserDataSource.getUserById(expectedUser.id)

        Assert.assertEquals(expectedUser, gottenUser)
    }

    @Test
    fun updateUserTest() {
        val userToUpdate = DEFAULT_USER_ENTITY
        val expectedUpdatedUser = userToUpdate.copy(username = "updated user")

        mLocalUserDataSource.insertUser(userToUpdate)
        mLocalUserDataSource.updateUser(expectedUpdatedUser)

        val gottenUpdatedUser = mLocalUserDataSource.getUserById(userToUpdate.id)

        Assert.assertEquals(expectedUpdatedUser, gottenUpdatedUser)
    }

    @Test
    fun deleteUserTest() {
        val userToDelete = DEFAULT_USER_ENTITY

        mLocalUserDataSource.insertUser(userToDelete)
        mLocalUserDataSource.deleteUser(userToDelete)

        val gottenUser = mLocalUserDataSource.getUserById(userToDelete.id)

        Assert.assertNull(gottenUser)
    }

//    @Test
//    fun saveUsersTest() {
//        val initUsers = generateUsers(count = 3)
//        val usersToSave = generateUsers(offset = 1, count = 3).toMutableList().apply {
//            this[0] = this[0].copy(username = "updated user")
//        }
//        val expectedUsers = usersToSave.toMutableList().apply {
//            add(0, initUsers.first())
//        }
//        val expectedUserIds = expectedUsers.map { it.id }
//
//        for (user in initUsers) mLocalUserDataSource.insertUser(user)
//
//        mLocalUserDataSource.saveUsers(usersToSave)
//
//        val gottenUsers = mLocalUserDataSource.getUsersByIds(expectedUserIds)
//
//        assertUserChunk(expectedUsers, gottenUsers)
//    }

//    private fun assertUserChunk(
//        expectedUserChunk: List<UserEntity>,
//        gottenUserChunk: List<UserEntity>?
//    ) {
//        Assert.assertNotNull(gottenUserChunk)
//        Assert.assertEquals(expectedUserChunk.size, gottenUserChunk!!.size)
//
//        for (expectedUser in expectedUserChunk)
//            Assert.assertTrue(gottenUserChunk.contains(expectedUser))
//    }
//
//    private fun generateUsers(
//        offset: Int = 0,
//        count: Int
//    ): List<UserEntity> {
//        return IntRange(offset, offset + count - 1).map {
//            val id = it.toLong()
//
//            UserEntity(id, "test user $id", "description $id",
//                0, 0, 0)
//        }
//    }
}