package com.qubacy.geoqq.ui.screen.mate.request.list.adapter

import com.example.carousel3dlib.general.Carousel3DContext
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.mate.request.model.MateRequest

interface MateRequestsAdapterCallback {
    fun getUserById(userId: Long): User
    fun onMateRequestSwiped(
        position: Int,
        mateRequest: MateRequest,
        direction: Carousel3DContext.SwipeDirection)
    fun onRequestListVerticalRoll(
        edgePosition: Int,
        direction: Carousel3DContext.RollingDirection)
}