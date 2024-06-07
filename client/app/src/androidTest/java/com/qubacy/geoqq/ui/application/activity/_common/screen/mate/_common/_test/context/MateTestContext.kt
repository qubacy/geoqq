package com.qubacy.geoqq.ui.application.activity._common.screen.mate._common._test.context

import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests._common.presentation.MateRequestPresentation

object MateTestContext {
    fun generateMateChatPresentation(
        user: UserPresentation,
        id: Long = 0L,
        newMessageCount: Int = 0,
        lastMessage: MateMessagePresentation? = null
    ): MateChatPresentation {
        return MateChatPresentation(id, user, newMessageCount, lastMessage, 0L)
    }

    fun generateMateMessagePresentation(
        user: UserPresentation,
        id: Long = 0L
    ): MateMessagePresentation {
        return MateMessagePresentation(id, user, "test message", "TIME", 0L)
    }

    fun generateMateRequestPresentation(
        user: UserPresentation,
        id: Long = 0L
    ): MateRequestPresentation {
        return MateRequestPresentation(id, user)
    }
}