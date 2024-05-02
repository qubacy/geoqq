package com.qubacy.geoqq.data.auth.repository.source.local.database

import androidx.room.Dao
import androidx.room.Query
import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.mate.chat.repository.source.local.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository.source.local.entity.MateMessageEntity
import com.qubacy.geoqq.data.user.repository.source.local.entity.UserEntity

@Dao
interface LocalAuthDatabaseDataSource : DataSource {
    @Query("DELETE FROM ${MateMessageEntity.TABLE_NAME}")
    fun dropMateMessages()

    @Query("DELETE FROM ${MateChatEntity.TABLE_NAME}")
    fun dropMateChats()

    @Query("DELETE FROM ${UserEntity.TABLE_NAME}")
    fun dropUsers()

    fun dropDataTables() {
        dropMateMessages()
        dropMateChats()
        dropUsers()
    }
}