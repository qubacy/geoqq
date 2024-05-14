package com.qubacy.geoqq.ui.application.activity._common.screen._common.component.input.text.watcher.error

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TextInputErrorCleanerWatcher(
    private val mTextInput: TextInputEditText,
    private val mTextInputWrapper: TextInputLayout
) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (mTextInputWrapper.error == null) return

        mTextInputWrapper.error = null
        mTextInputWrapper.isErrorEnabled = false
    }
}