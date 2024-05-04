package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.MateRequestsFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.chunk.insert.InsertRequestsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.request.RemoveRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.answer.ReturnAnsweredRequestUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.chunk.update.UpdateRequestsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation.request.UpdateRequestUiOperation

class MateRequestsUiOperationHandler(
    fragment: MateRequestsFragment
) : UiOperationHandler<MateRequestsFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        when (uiOperation::class) {
            InsertRequestsUiOperation::class -> {
                uiOperation as InsertRequestsUiOperation

                fragment.onMateRequestsFragmentInsertRequests(
                    uiOperation.requests, uiOperation.position)
            }
            UpdateRequestsUiOperation::class -> {
                uiOperation as UpdateRequestsUiOperation

                fragment.onMateRequestsFragmentUpdateRequests(
                    uiOperation.requests, uiOperation.position)
            }
            UpdateRequestUiOperation::class -> {
                uiOperation as UpdateRequestUiOperation

                fragment.onMateRequestsFragmentUpdateRequest(
                    uiOperation.request, uiOperation.position)
            }
            RemoveRequestUiOperation::class -> {
                uiOperation as RemoveRequestUiOperation

                fragment.onMateRequestsFragmentRemoveRequest(uiOperation.position)
            }
            ReturnAnsweredRequestUiOperation::class -> {
                uiOperation as ReturnAnsweredRequestUiOperation

                fragment.onMateRequestsFragmentReturnAnsweredRequest(uiOperation.position)
            }
            else -> return false
        }

        return true
    }
}