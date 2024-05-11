package com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.dao.LocalErrorDataSourceDao
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.dao.model.toError
import java.util.Locale
import javax.inject.Inject

class LocalErrorDatabaseDataSourceImpl @Inject constructor(
    private val mLocalErrorDataSourceDao: LocalErrorDataSourceDao
) : LocalErrorDatabaseDataSource {
    override fun getError(id: Long): Error {
        val lang = Locale.getDefault().language

        return mLocalErrorDataSourceDao.getErrorById(id, lang)?.toError()
            ?: throw IllegalStateException()
    }
}