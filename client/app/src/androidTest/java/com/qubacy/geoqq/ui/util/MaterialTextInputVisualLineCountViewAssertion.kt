package com.qubacy.geoqq.ui.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlin.math.abs

class MaterialTextInputVisualLineCountViewAssertion(
    private val lineCount: Int = 1
) : ViewAssertion {
    override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
        if (view == null)
            throw NoMatchingViewException.Builder().build()
        if (view !is TextInputEditText)
            throw NoMatchingViewException.Builder().build()

        val materialTextInput = view as TextInputEditText
        val materialTextInputLayout = view.parent as ViewGroup

        // todo: shitty evaluation. is there a better approach?
        val textCursorHeight = materialTextInput.textCursorDrawable!!.bounds.height()
        val totalLinesHeightPx = textCursorHeight * lineCount
//            materialTextInput.lineHeight * lineCount +
//            materialTextInput.paddingTop + materialTextInput.paddingBottom +
//            materialTextInput.marginTop + materialTextInput.marginBottom

        val absHeightsDifference = abs(materialTextInput.height - totalLinesHeightPx)

        if (absHeightsDifference >= textCursorHeight)
            throw NoMatchingViewException.Builder()
                .withCause(Throwable("Line count was different!")).build()
    }
}