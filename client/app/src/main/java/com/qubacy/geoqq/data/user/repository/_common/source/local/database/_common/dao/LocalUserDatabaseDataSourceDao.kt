package com.qubacy.geoqq.data.user.repository._common.source.local.database._common.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity

@Dao
interface LocalUserDatabaseDataSourceDao {
    @Query("SELECT * FROM ${UserEntity.TABLE_NAME} WHERE ${UserEntity.ID_PARAM_NAME} = :userId")
    fun getUserById(userId: Long): UserEntity?

    @Update()
    fun updateUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

    @Delete()
    fun deleteUser(user: UserEntity)
}