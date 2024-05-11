package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.operation.request

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation

class UpdateRequestUiOperation(
    val position: Int,
    val request: MateRequestPresentation
) : UiOperation {

}