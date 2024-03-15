package com.qubacy.geoqq.data.error.repository

import com.qubacy.geoqq.data._common.repository._common.DataRepository
import com.qubacy.geoqq.data.error.repository.source.local.LocalErrorDataSource
import com.qubacy.geoqq.data.error.repository.source.local.model.toError
import com.qubacy.geoqq._common.error.Error
import java.util.Locale
import javax.inject.Inject

open class ErrorDataRepository @Inject constructor(
    private val mLocalErrorDataSource: LocalErrorDataSource
) : DataRepository {
    open fun getError(id: Long): Error {
        val lang = Locale.getDefault().language

        return mLocalErrorDataSource.getErrorById(id, lang)?.toError()
            ?: throw IllegalStateException()
    }
}