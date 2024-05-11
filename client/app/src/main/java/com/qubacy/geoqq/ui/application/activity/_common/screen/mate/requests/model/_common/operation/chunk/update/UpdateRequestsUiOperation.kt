package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.chunk.update

import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.chunk._common.RequestsUiOperation

class UpdateRequestsUiOperation(
    position: Int,
    requests: List<MateRequestPresentation>
) : RequestsUiOperation(position, requests) {

}