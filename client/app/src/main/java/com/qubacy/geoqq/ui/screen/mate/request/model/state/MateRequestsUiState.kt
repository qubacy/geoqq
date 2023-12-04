package com.qubacy.geoqq.ui.screen.mate.request.model.state

import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.mate.request.model.MateRequest
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.state.OperationUiState

class MateRequestsUiState(
    val mateRequests: List<MateRequest>,
    val users: List<User>,
    uiOperations: List<UiOperation>
) : OperationUiState(uiOperations) {

}