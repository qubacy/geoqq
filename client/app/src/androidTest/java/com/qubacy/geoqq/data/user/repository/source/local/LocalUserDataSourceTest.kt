package com.qubacy.geoqq.data.user.repository.source.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.data.common.repository.source.local.DatabaseSourceTest
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalUserDataSourceTest() : DatabaseSourceTest() {
    private lateinit var mLocalUserDataSource: LocalUserDataSource

    @Before
    override fun setup() {
        super.setup()

        mLocalUserDataSource = mDatabase.getUserDAO()
    }

    @Test
    fun getUserByIdAndInsertTest() {
        val user = UserEntity(0, "test", "something", 0, 0)

        mLocalUserDataSource.insertUser(user)

        val userEntity = mLocalUserDataSource.getUserById(user.id)

        Assert.assertEquals(user, userEntity)

        mLocalUserDataSource.deleteUser(user)
    }

    @Test
    fun updateUserTest() {
        val user = UserEntity(0, "test", "something", 0, 0)

        mLocalUserDataSource.insertUser(user)

        var userEntity = mLocalUserDataSource.getUserById(user.id)

        Assert.assertEquals(user, userEntity)

        val updatedUser = UserEntity(0, "testUpdated", "something", 0, 0)

        mLocalUserDataSource.updateUser(updatedUser)

        userEntity = mLocalUserDataSource.getUserById(user.id)

        Assert.assertEquals(updatedUser, userEntity)

        mLocalUserDataSource.deleteUser(user)
    }

    @Test
    fun deleteUserTest() {
        val user = UserEntity(0, "test", "something", 0, 0)

        mLocalUserDataSource.insertUser(user)
        mLocalUserDataSource.deleteUser(user)

        val userEntity = mLocalUserDataSource.getUserById(user.id)

        Assert.assertNull(userEntity)
    }
}