package com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common

import com.qubacy.geoqq._common.model.error._common.Error

interface LocalErrorDatabaseDataSource {
    fun getError(id: Long): Error
}