package com.qubacy.geoqq.ui.util

import android.view.View
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import com.google.android.material.textfield.TextInputEditText
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

        // todo: shitty evaluation. is there a better approach?

        val totalLinesHeightPx = view.lineHeight * lineCount + view.paddingTop + view.paddingBottom
        val absHeightsDifference = abs(materialTextInput.height - totalLinesHeightPx)

        if (absHeightsDifference >= view.lineHeight)
            throw NoMatchingViewException.Builder()
                .withCause(Throwable("Line count was different!")).build()
    }
}