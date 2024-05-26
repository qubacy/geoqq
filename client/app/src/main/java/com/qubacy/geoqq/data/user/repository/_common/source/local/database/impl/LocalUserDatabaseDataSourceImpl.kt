package com.qubacy.geoqq.data.user.repository._common.source.local.database.impl

import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.LocalUserDatabaseDataSource
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao.LocalUserDatabaseDataSourceDao
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity
import javax.inject.Inject

class LocalUserDatabaseDataSourceImpl @Inject constructor(
    private val mLocalUserDatabaseDataSourceDao: LocalUserDatabaseDataSourceDao
) : LocalUserDatabaseDataSource {
    override fun getUserById(userId: Long): UserEntity? {
        return mLocalUserDatabaseDataSourceDao.getUserById(userId)
    }

    override fun getUsersByIds(userIds: List<Long>): List<UserEntity>? {
        val localUsers = mutableListOf<UserEntity>()

        for (userId in userIds) {
            val localUser = getUserById(userId) ?: return null

            localUsers.add(localUser)
        }

        return localUsers
    }

    override fun updateUser(user: UserEntity) {
        return mLocalUserDatabaseDataSourceDao.updateUser(user)
    }

    override fun insertUser(user: UserEntity) {
        return mLocalUserDatabaseDataSourceDao.insertUser(user)
    }

    override fun deleteUser(user: UserEntity) {
        return mLocalUserDatabaseDataSourceDao.deleteUser(user)
    }

    override fun saveUsers(users: List<UserEntity>) {
        for (user in users) {
            val localUser = getUserById(user.id)

            if (localUser != null) updateUser(user)
            else insertUser(user)
        }
    }
}