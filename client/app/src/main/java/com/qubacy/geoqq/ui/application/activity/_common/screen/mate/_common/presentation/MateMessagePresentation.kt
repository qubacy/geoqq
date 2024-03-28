package com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation

import com.qubacy.geoqq.domain.mate.message.model.MateMessage
import com.qubacy.geoqq.ui._common.util.time.TimeUtils
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.toUserPresentation
import java.util.Locale
import java.util.TimeZone

data class MateMessagePresentation(
    val id: Long,
    val user: UserPresentation,
    val text: String,
    val timestamp: String
) {

}

fun MateMessage.toMateMessagePresentation(): MateMessagePresentation {
    val timestamp = TimeUtils.longToHoursMinutesSecondsFormattedString(
        time, Locale.getDefault(), TimeZone.getDefault())

    return MateMessagePresentation(id, user.toUserPresentation(), text, timestamp)
}