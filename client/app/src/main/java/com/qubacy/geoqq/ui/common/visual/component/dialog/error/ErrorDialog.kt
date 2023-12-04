package com.qubacy.geoqq.ui.common.visual.component.dialog.error

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.qubacy.geoqq.R

class ErrorDialog(
    context: Context
) : AlertDialog(context) {
    class Builder(
        errorMessage: String,
        context: Context,
        onDismiss: Runnable
    ) : AlertDialog.Builder(
        context,
        R.style.MaterialAlertDialog
    ) {
        init {
            setTitle(R.string.error_dialog_title)
            setMessage(errorMessage)
            setNeutralButton(R.string.component_dialog_error_neutral_button_caption) {
                dialog, which -> onDismiss.run()
            }
            setOnDismissListener {
                onDismiss.run()
            }
        }
    }
}