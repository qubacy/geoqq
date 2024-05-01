package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.interlocutor.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.interlocutor.InterlocutorFragment
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.interlocutor.model.operation.UpdateInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.operation.handler._common.UiOperationHandler

class InterlocutorUiOperationHandler(
    fragment: InterlocutorFragment
) : UiOperationHandler<InterlocutorFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        when (uiOperation::class) {
            ShowInterlocutorDetailsUiOperation::class ->
                fragment.openInterlocutorDetailsSheet(
                    (uiOperation as ShowInterlocutorDetailsUiOperation).interlocutor)
            UpdateInterlocutorDetailsUiOperation::class ->
                fragment.adjustInterlocutorFragmentUiWithInterlocutor(
                    (uiOperation as UpdateInterlocutorDetailsUiOperation).interlocutor)
            else -> return false
        }

        return true
    }
}