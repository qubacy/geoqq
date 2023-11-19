package com.qubacy.geoqq.applicaion.container.mate.requests

import com.qubacy.geoqq.domain.mate.request.MateRequestsUseCase
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModelFactory

class MateRequestsContainer(
    val mateRequestsUseCase: MateRequestsUseCase,
) {
    val mateRequestsViewModelFactory = MateRequestsViewModelFactory(mateRequestsUseCase)
}