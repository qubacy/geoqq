package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.mateable.model

import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation

interface MateableViewModel {
    fun isInterlocutorMateableOrDeletable(interlocutor: UserPresentation): Boolean
}