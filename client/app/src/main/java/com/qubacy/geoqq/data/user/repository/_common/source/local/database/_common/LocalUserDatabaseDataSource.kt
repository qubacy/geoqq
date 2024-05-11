package com.qubacy.geoqq.data.user.repository._common.source.local.database._common

import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity

interface LocalUserDatabaseDataSource {
    fun getUserById(userId: Long): UserEntity?
    fun getUsersByIds(userIds: List<Long>): List<UserEntity>?
    fun updateUser(user: UserEntity)
    fun insertUser(user: UserEntity)
    fun deleteUser(user: UserEntity)
    fun saveUsers(users: List<UserEntity>)
}