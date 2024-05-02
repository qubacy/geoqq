package com.qubacy.geoqq.data._common.repository._common.source.local.database.error

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.LocalErrorDataSourceDao
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.dao.model.toError
import java.util.Locale
import javax.inject.Inject

class LocalErrorDataSource @Inject constructor(
    private val mLocalErrorDataSourceDao: LocalErrorDataSourceDao
) : DataSource {
    open fun getError(id: Long): Error {
        val lang = Locale.getDefault().language

        return mLocalErrorDataSourceDao.getErrorById(id, lang)?.toError()
            ?: throw IllegalStateException()
    }
}