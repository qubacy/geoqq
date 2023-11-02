package com.qubacy.geoqq.data.user.repository.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qubacy.geoqq.data.user.model.DataUser

@Entity(tableName = UserEntity.TABLE_NAME)
data class UserEntity(
    @PrimaryKey()
    @ColumnInfo(name = ID_PARAM_NAME) val userId: Int,
    @ColumnInfo(name = USERNAME_PARAM_NAME) val username: String,
    @ColumnInfo(name = DESCRIPTION_PARAM_NAME) val description: String,
    @ColumnInfo(name = AVATAR_ID_PARAM_NAME) val avatarId: Long,
    @ColumnInfo(name = IS_MATE_PARAM_NAME) val isMate: Boolean
) {
    companion object {
        const val TABLE_NAME = "User"

        const val ID_PARAM_NAME = "id"
        const val USERNAME_PARAM_NAME = "username"
        const val DESCRIPTION_PARAM_NAME = "description"
        const val AVATAR_ID_PARAM_NAME = "avatar_id"
        const val IS_MATE_PARAM_NAME = "is_mate"
    }
}

fun UserEntity.toDataUser(): DataUser {
    return DataUser(username, description, avatarId, isMate)
}