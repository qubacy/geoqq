package com.qubacy.geoqq.applicaion.common.container.mate.requests

import com.qubacy.geoqq.domain.mate.request.MateRequestsUseCase
import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModelFactory

class MateRequestsContainerImpl(
    private val mateRequestsUseCase: MateRequestsUseCase,
) : MateRequestsContainer() {
    override val mateRequestsViewModelFactory = MateRequestsViewModelFactory(mateRequestsUseCase)
}