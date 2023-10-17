package com.qubacy.geoqq.ui.screen.mate.request.model.state

import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.mates.request.entity.MateRequest
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.common.model.state.OperationUiState

class MateRequestsUiState(
    val mateRequests: List<MateRequest>,
    val users: List<User>,
    uiOperations: List<UiOperation>
) : OperationUiState(uiOperations) {

}