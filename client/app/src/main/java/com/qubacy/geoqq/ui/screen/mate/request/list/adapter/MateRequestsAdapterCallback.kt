package com.qubacy.geoqq.ui.screen.mate.request.list.adapter

import com.example.carousel3dlib.general.Carousel3DContext
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.mates.request.entity.MateRequest

interface MateRequestsAdapterCallback {
    fun getUserById(userId: Long): User
    fun onMateRequestSwiped(
        mateRequest: MateRequest,
        direction: Carousel3DContext.SwipeDirection)
}