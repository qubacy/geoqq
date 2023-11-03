package com.qubacy.geoqq.data.error.repository.source.local

import androidx.room.Dao
import androidx.room.Query
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import com.qubacy.geoqq.data.error.repository.source.local.model.ErrorEntity

@Dao
interface LocalErrorDataSource : DataSource {
    @Query("SELECT * FROM ${ErrorEntity.TABLE_NAME} " +
           "WHERE ${ErrorEntity.ID_PARAM_NAME} = :errorId " +
           "AND ${ErrorEntity.LANGUAGE_PARAM_NAME} = :languageCode")
    fun getErrorById(errorId: Long, languageCode: String): ErrorEntity
}