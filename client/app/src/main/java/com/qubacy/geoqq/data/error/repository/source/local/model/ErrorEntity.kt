package com.qubacy.geoqq.data.error.repository.source.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.qubacy.geoqq.common.error.common.Error

@Entity(
    tableName = ErrorEntity.TABLE_NAME,
    primaryKeys = [ErrorEntity.ID_PARAM_NAME, ErrorEntity.LANGUAGE_PARAM_NAME]
)
data class ErrorEntity(
    @ColumnInfo(name = ID_PARAM_NAME) val id: Long,
    @ColumnInfo(name = MESSAGE_PARAM_NAME) val message: String,
    @ColumnInfo(name = LANGUAGE_PARAM_NAME) val lang: String,
    @ColumnInfo(
        name = IS_CRITICAL_PARAM_NAME,
        defaultValue = DEFAULT_IS_CRITICAL_VALUE
    ) val isCritical: Int
) {
    companion object {
        const val TABLE_NAME = "Error"

        const val ID_PARAM_NAME = "id"
        const val MESSAGE_PARAM_NAME = "message"
        const val LANGUAGE_PARAM_NAME = "lang"
        const val IS_CRITICAL_PARAM_NAME = "is_critical"

        const val DEFAULT_IS_CRITICAL_VALUE = "0"
    }
}

fun ErrorEntity.toError(): Error {
    return Error(id, message, isCritical == 1)
}