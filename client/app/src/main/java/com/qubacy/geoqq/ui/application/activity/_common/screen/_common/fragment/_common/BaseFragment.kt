package com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment._common

import androidx.core.graphics.Insets
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common.model.error.Error
import com.qubacy.geoqq.ui._common.util.view.extension.catchViewInsets

abstract class BaseFragment<ViewBindingType : ViewBinding>() : Fragment() {
    companion object {
        const val TAG = "BaseFragment"
    }

    protected lateinit var mBinding: ViewBindingType

    private var mErrorDialog: AlertDialog? = null
    private var mRequestDialog: AlertDialog? = null

    private var mMessageSnackbar: Snackbar? = null
    protected var mSnackbarAnchorView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivityResultLaunchers()
    }

    /**
     * Should be used for initializing ActivityResultLaunchers;
     */
    protected open fun initActivityResultLaunchers() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = createBinding(inflater, container)

        return mBinding.root
    }

    protected abstract fun createBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ): ViewBindingType

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // todo: mb it's the time to get rid of this in the base class?:
        view.catchViewInsets(viewInsetsToCatch()) { insets, insetsRes ->
            adjustViewToInsets(insets, insetsRes)
        }
    }

    override fun onStop() {
        mErrorDialog?.dismiss()
        mRequestDialog?.dismiss()
        mMessageSnackbar?.dismiss()

        super.onStop()
    }

    protected open fun viewInsetsToCatch(): Int {
        return WindowInsetsCompat.Type.statusBars() or
               WindowInsetsCompat.Type.navigationBars()
    }

    protected open fun adjustViewToInsets(insets: Insets, insetsRes: WindowInsetsCompat) { }

    open fun onPopupMessageOccurred(
        message: String,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        mMessageSnackbar = Snackbar.make(requireContext(), requireView(), message, duration)
            .setAnchorView(mSnackbarAnchorView)

        mMessageSnackbar!!.show()
    }

    fun onPopupMessageOccurred(
        @StringRes message: Int,
        duration: Int = Toast.LENGTH_SHORT
    ) {
        onPopupMessageOccurred(getString(message), duration)
    }

    protected open fun onErrorHandled() { }

    open fun onErrorOccurred(error: Error) {
        val onDismissedWithButton = {
            onErrorDismissed(error)
            onErrorHandled()
        }

        mErrorDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.component_error_dialog_title_text)
            .setMessage(error.message)
            .setNeutralButton(R.string.component_error_dialog_button_neutral_caption) { _, _ -> onDismissedWithButton() }
            .create()

        mErrorDialog!!.show()
    }

    open fun showRequestDialog(
        @StringRes messageResId: Int,
        onPositiveButtonClicked: () -> Unit,
        onNegativeButtonClicked: (() -> Unit)? = null
    ) {
        mRequestDialog = MaterialAlertDialogBuilder(requireContext())
            .setMessage(messageResId)
            .setPositiveButton(R.string.component_request_dialog_button_positive_caption) { _, _ ->
                onPositiveButtonClicked()
            }
            .setNegativeButton(R.string.component_request_dialog_button_negative_caption) { _, _ ->
                onNegativeButtonClicked?.invoke()
            }
            .create()

        mRequestDialog!!.show()
    }

    open fun onErrorDismissed(error: Error) {
        if (error.isCritical) {
            requireActivity().finishAndRemoveTask()
        }
    }
}