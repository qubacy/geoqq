package com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler.common.callback

import com.qubacy.geoqq._common.model.error._common.Error

interface UpdateCommonErrorHandlerCallback {
    fun dropCommonUpdateError(error: Error)
}