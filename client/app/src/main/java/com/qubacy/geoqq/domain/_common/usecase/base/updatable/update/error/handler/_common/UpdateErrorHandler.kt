package com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler._common

import com.qubacy.geoqq._common.model.error._common.Error

interface UpdateErrorHandler {
    fun handleError(error: Error): Boolean
}