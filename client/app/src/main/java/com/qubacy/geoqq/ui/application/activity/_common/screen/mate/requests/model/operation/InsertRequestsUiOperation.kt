package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.operation

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation

class InsertRequestsUiOperation(
    val position: Int,
    val requests: List<MateRequestPresentation>
) : UiOperation {

}