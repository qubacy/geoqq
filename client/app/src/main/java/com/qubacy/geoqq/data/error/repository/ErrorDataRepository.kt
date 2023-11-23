package com.qubacy.geoqq.data.error.repository

import com.qubacy.geoqq.data.common.repository.common.DataRepository
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.error.repository.result.GetErrorForLanguageResult
import com.qubacy.geoqq.data.error.repository.result.GetErrorForLanguageWithDatabaseResult
import com.qubacy.geoqq.data.error.repository.source.local.LocalErrorDataSource
import com.qubacy.geoqq.data.error.repository.source.local.model.ErrorEntity
import com.qubacy.geoqq.data.error.repository.source.local.model.toError

open class ErrorDataRepository(
    val localErrorDataSource: LocalErrorDataSource
) : DataRepository() {
    private fun getErrorForLanguageWithDatabase(
        errorId: Long,
        languageCode: String
    ): Result {
        var errorEntity: ErrorEntity? = null

        try {
            errorEntity = localErrorDataSource.getErrorById(errorId, languageCode)

        } catch (e: Exception) {
            // todo: what to do??
            throw e
        }

        if (errorEntity == null) throw IllegalStateException()

        return GetErrorForLanguageWithDatabaseResult(errorEntity.toError())
    }

    suspend fun getErrorForLanguage(
        errorId: Long,
        languageCode: String
    ): Result {
        val getUserWithDatabaseResult = getErrorForLanguageWithDatabase(errorId, languageCode)

        if (getUserWithDatabaseResult is ErrorResult) return getUserWithDatabaseResult
        if (getUserWithDatabaseResult is InterruptionResult) return getUserWithDatabaseResult

        return GetErrorForLanguageResult(
            (getUserWithDatabaseResult as GetErrorForLanguageWithDatabaseResult).error)
    }

    override fun interrupt() {
        // todo: no action??
    }

    override fun reset() {

    }
}