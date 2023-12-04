package com.qubacy.geoqq.ui.common.visual.component.combobox.view

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class ComboBoxView(
    context: Context,
    attributeSet: AttributeSet
) : MaterialAutoCompleteTextView(context, attributeSet) {
    companion object {
        const val POSITION_NOT_DEFINED = -1
    }

    var currentItemPosition: Int = POSITION_NOT_DEFINED
        set(value) {
            if (!isPositionValid(value)) return

            field = value

            displayTextForCurrentPosition()
        }

    private fun isPositionValid(position: Int): Boolean {
        if (adapter == null)
            throw IllegalStateException()

        return (position >= 0 && position < adapter.count)
    }

    private fun displayTextForCurrentPosition() {
        setText(adapter.getItem(currentItemPosition).toString())
    }
}