package com.qubacy.geoqq.applicaion.common.container.mate.requests

import com.qubacy.geoqq.ui.screen.mate.request.model.MateRequestsViewModelFactory

abstract class MateRequestsContainer() {
    abstract val mateRequestsViewModelFactory: MateRequestsViewModelFactory
}