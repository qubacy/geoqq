package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat

import androidx.annotation.CallSuper
import com.qubacy.geoqq.R
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.popup.PopupFragment

interface ChatFragment {
    fun getPopupFragmentForChatFragment(): PopupFragment

    fun onChatFragmentMessageSent() {}

    @CallSuper
    fun onChatFragmentMateRequestSent() {
        getPopupFragmentForChatFragment()
            .onPopupMessageOccurred(R.string.fragment_chat_snackbar_message_mate_request_sent)
    }
}