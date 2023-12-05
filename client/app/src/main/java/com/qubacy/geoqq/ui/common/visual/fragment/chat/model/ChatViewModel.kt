package com.qubacy.geoqq.ui.common.visual.fragment.chat.model

interface ChatViewModel {
    fun getUserDetails(userId: Long)
    fun createMateRequest(userId: Long)
}