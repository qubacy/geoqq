package com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler.common

import android.util.Log
import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.error.type.DataHttpWebSocketErrorType
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler._common.UpdateErrorHandler
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler.common.callback.UpdateCommonErrorHandlerCallback

class UpdateCommonErrorHandler(
    callback: UpdateCommonErrorHandlerCallback
) : UpdateErrorHandler {
    companion object {
        const val TAG = "UpdateCommonErrorHndlr"
    }

    private val mCallback: UpdateCommonErrorHandlerCallback = callback

    override fun handleError(error: Error): Boolean {
        when (error.id) {
            DataHttpWebSocketErrorType.WEB_SOCKET_FAILURE.getErrorCode() ->
                processWebSocketFailureError(error)
            else -> return false
        }

        return true
    }

    private fun processWebSocketFailureError(error: Error) {
        Log.d(TAG, "processWebSocketFailureError(): error = $error;")

        mCallback.dropCommonUpdateError(error)
    }
}