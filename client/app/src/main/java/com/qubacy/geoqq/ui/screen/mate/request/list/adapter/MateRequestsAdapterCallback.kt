package com.qubacy.geoqq.ui.screen.mate.request.list.adapter

import com.example.carousel3dlib.general.Carousel3DContext
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.mate.request.model.MateRequest

interface MateRequestsAdapterCallback {
    fun getUserById(userId: Long): User
    fun onMateRequestSwiped(
        mateRequest: MateRequest,
        direction: Carousel3DContext.SwipeDirection)
}