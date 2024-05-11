package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.GeoChatFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.operation.AddGeoMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.operation.UpdateGeoMessagesUiOperation

class GeoChatUiOperationHandler(
    fragment: GeoChatFragment
) : UiOperationHandler<GeoChatFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        when (uiOperation::class) {
            AddGeoMessagesUiOperation::class -> {
                uiOperation as AddGeoMessagesUiOperation

                fragment.onGeoChatFragmentAddGeoMessages(uiOperation.messages)
            }
            UpdateGeoMessagesUiOperation::class -> {
                uiOperation as UpdateGeoMessagesUiOperation

                fragment.onGeoChatFragmentUpdateGeoMessages(
                    uiOperation.positions, uiOperation.messages)
            }
            else -> return false
        }

        return true
    }
}