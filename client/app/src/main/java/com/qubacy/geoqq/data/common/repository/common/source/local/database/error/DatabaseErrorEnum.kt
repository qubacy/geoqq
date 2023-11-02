package com.qubacy.geoqq.data.common.repository.common.source.local.database.error

import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.ErrorBase
import com.qubacy.geoqq.common.error.common.TypedErrorBase
import com.qubacy.geoqq.common.error.local.LocalError

enum class DatabaseErrorEnum(val error: TypedErrorBase) {
    UNKNOWN_DATABASE_ERROR(
        LocalError(R.string.error_unknown_database_error, ErrorBase.Level.CRITICAL)
    ),
    ;
}