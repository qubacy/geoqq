package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor

import androidx.annotation.CallSuper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.WindowInsetsCompat
import com.qubacy.geoqq.ui.application.activity._common.screen._common.component.bottomsheet.user.view.UserBottomSheetViewContainer
import com.qubacy.geoqq.ui.application.activity._common.screen._common.component.bottomsheet.user.view.UserBottomSheetViewContainerCallback
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation

interface InterlocutorFragment {
    fun openInterlocutorDetailsSheet(
        interlocutor: UserPresentation
    ) {
        if (getInterlocutorDetailsSheet() == null) initInterlocutorDetailsSheet()

        setupInterlocutorDetailsSheet(interlocutor)
        getInterlocutorDetailsSheet()!!.open()
    }

    fun getInterlocutorDetailsSheet(): UserBottomSheetViewContainer?

    fun setupInterlocutorDetailsSheet(
        interlocutor: UserPresentation
    ) {
        val interlocutorDetailsSheet = getInterlocutorDetailsSheet()
        val isMateButtonEnabled = isInterlocutorDetailsMateButtonEnabled(interlocutor)
        val isMateButtonVisible = isInterlocutorDetailsMateButtonVisible(interlocutor)

        interlocutorDetailsSheet?.apply {
            setMateButtonEnabled(isMateButtonEnabled)
            setMateButtonVisible(isMateButtonVisible)
            setUserData(interlocutor)
        }
    }

    fun initInterlocutorDetailsSheet()

    fun createInterlocutorDetailsSheet(): UserBottomSheetViewContainer {
        val expandedBottomSheetHeight = getInterlocutorDetailsSheetExpandedHeight()
        val collapsedBottomSheetHeight = getInterlocutorDetailsSheetCollapsedHeight()

        val parent = getInterlocutorDetailsSheetParent()
        val callback = getInterlocutorDetailsSheetCallback()

        val interlocutorDetailsSheet = UserBottomSheetViewContainer(
            parent.context,
            parent,
            expandedBottomSheetHeight,
            collapsedBottomSheetHeight,
            callback
        )

        postCreateInterlocutorDetailsSheet(interlocutorDetailsSheet)
        parent.addView(interlocutorDetailsSheet.getView())

        return interlocutorDetailsSheet
    }

    fun getInterlocutorDetailsSheetExpandedHeight(): Int
    fun getInterlocutorDetailsSheetCollapsedHeight(): Int {
        return getInterlocutorDetailsSheetExpandedHeight() / 2
    }

    fun getInterlocutorDetailsSheetParent(): CoordinatorLayout
    fun getInterlocutorDetailsSheetCallback(): UserBottomSheetViewContainerCallback

    fun postCreateInterlocutorDetailsSheet(interlocutorDetailsSheet: UserBottomSheetViewContainer) {
        val insets = getInterlocutorDetailsSheetInsets() ?: return

        interlocutorDetailsSheet.adjustToInsets(insets)
    }

    fun getInterlocutorDetailsSheetInsets(): WindowInsetsCompat? = null

    fun isInterlocutorDetailsMateButtonEnabled(interlocutor: UserPresentation): Boolean
    fun isInterlocutorDetailsMateButtonVisible(interlocutor: UserPresentation): Boolean = true

    @CallSuper
    fun adjustInterlocutorFragmentUiWithInterlocutor(interlocutor: UserPresentation) {
        setupInterlocutorDetailsSheet(interlocutor)
    }

    fun closeInterlocutorDetailsSheet() {
        getInterlocutorDetailsSheet()?.close()
    }
}