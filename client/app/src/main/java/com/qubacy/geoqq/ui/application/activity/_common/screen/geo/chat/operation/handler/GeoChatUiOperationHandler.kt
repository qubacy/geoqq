package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.GeoChatFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.operation.AddGeoMessagesUiOperation

class GeoChatUiOperationHandler(
    fragment: GeoChatFragment
) : UiOperationHandler<GeoChatFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        if (uiOperation !is AddGeoMessagesUiOperation) return false

        fragment.onGeoChatFragmentAddGeoMessages(uiOperation.messages)

        return true
    }
}