package com.qubacy.geoqq.ui.screen.mate.chat.list.adapter

import com.qubacy.geoqq.ui.common.fragment.chat.component.list.adapter.ChatAdapterCallback

interface MateChatAdapterCallback : ChatAdapterCallback {
    fun onEdgeReached()
}