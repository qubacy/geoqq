package com.qubacy.geoqq.data.error.repository.source.local

import androidx.room.Dao
import androidx.room.Query
import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.error.repository.source.local.model.ErrorEntity

@Dao
interface LocalErrorDataSource : DataSource {
    @Query(
        "SELECT * " +
        "FROM ${ErrorEntity.TABLE_NAME} " +
        "WHERE ${ErrorEntity.ID_PROP_NAME} = :id AND ${ErrorEntity.LANG_PROP_NAME} = :lang"
    )
    fun getErrorById(id: Long, lang: String): ErrorEntity?
}