package com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao

import androidx.room.Dao
import androidx.room.Query
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.model.ErrorEntity

@Dao
interface LocalErrorDataSourceDao {
    @Query(
        "SELECT * " +
        "FROM ${ErrorEntity.TABLE_NAME} " +
        "WHERE ${ErrorEntity.ID_PROP_NAME} = :id AND ${ErrorEntity.LANG_PROP_NAME} = :lang"
    )
    fun getErrorById(id: Long, lang: String): ErrorEntity?
}