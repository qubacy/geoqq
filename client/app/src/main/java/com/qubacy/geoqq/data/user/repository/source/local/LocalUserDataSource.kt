package com.qubacy.geoqq.data.user.repository.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
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
}