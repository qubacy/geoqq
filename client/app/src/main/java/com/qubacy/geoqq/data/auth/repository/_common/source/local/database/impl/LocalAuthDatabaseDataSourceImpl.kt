package com.qubacy.geoqq.data.auth.repository._common.source.local.database.impl

import androidx.room.Dao
import androidx.room.Query
import com.qubacy.geoqq.data.auth.repository._common.source.local.database._common.LocalAuthDatabaseDataSource
import com.qubacy.geoqq.data.mate.chat.repository._common.source.local.database._common.entity.MateChatEntity
import com.qubacy.geoqq.data.mate.message.repository._common.source.local.database._common.entity.MateMessageEntity
import com.qubacy.geoqq.data.user.repository._common.source.local.database._common.entity.UserEntity

@Dao
interface LocalAuthDatabaseDataSourceImpl : LocalAuthDatabaseDataSource {
    @Query("DELETE FROM ${MateMessageEntity.TABLE_NAME}")
    fun dropMateMessages()

    @Query("DELETE FROM ${MateChatEntity.TABLE_NAME}")
    fun dropMateChats()

    @Query("DELETE FROM ${UserEntity.TABLE_NAME}")
    fun dropUsers()

    override fun dropDataTables() {
        dropMateMessages()
        dropMateChats()
        dropUsers()
    }
}