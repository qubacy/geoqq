package com.qubacy.geoqq.data.user.repository.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity

@Dao
interface LocalUserDataSource : DataSource {
    @Query("SELECT * FROM ${UserEntity.TABLE_NAME} WHERE ${UserEntity.ID_PARAM_NAME} = :userId")
    fun getUserById(userId: Long): UserEntity?

    @Update()
    fun updateUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Delete()
    fun deleteUser(user: UserEntity)

    fun getUsersByIds(userIds: List<Long>): List<UserEntity>? {
        val localUsers = mutableListOf<UserEntity>()

        for (userId in userIds) {
            val localUser = getUserById(userId) ?: return null

            localUsers.add(localUser)
        }

        return localUsers
    }

    fun saveUsers(users: List<UserEntity>) {
        for (user in users) {
            val localUser = getUserById(user.id)

            if (localUser != null) updateUser(user)
            else insertUser(user)
        }
    }
}