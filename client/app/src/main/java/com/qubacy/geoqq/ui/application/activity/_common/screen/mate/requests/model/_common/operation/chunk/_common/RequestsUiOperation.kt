package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.chunk._common

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation

abstract class RequestsUiOperation(
    val position: Int,
    val requests: List<MateRequestPresentation>
) : UiOperation {

}