package com.qubacy.geoqq.ui.screen.geochat.auth.common

import android.os.Bundle
import android.view.View
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.ShowErrorUiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.geochat.auth.common.model.AuthViewModel
import com.qubacy.geoqq.ui.screen.geochat.auth.common.model.operation.AuthorizeUiOperation
import com.qubacy.geoqq.ui.screen.geochat.auth.common.model.state.AuthUiState

abstract class AuthFragment : WaitingFragment() {
    abstract override val mModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.authUiState.observe(viewLifecycleOwner) {
            if (it == null) return@observe

            onAuthStateGotten(it)
        }
    }

    protected open fun onAuthStateGotten(uiState: AuthUiState) {
        // todo: is there a need to check the token for validity here?

        for (uiOperation in uiState.newUiOperations) {
            processUiOperation(uiOperation)
        }
    }

    protected open fun processUiOperation(uiOperation: UiOperation) {
        when (uiOperation::class.java) {
            AuthorizeUiOperation::class.java -> {
                val authorizeUiOperation = uiOperation as AuthorizeUiOperation

                processAuthorizeUiOperation(authorizeUiOperation)
            }
            ShowErrorUiOperation::class.java -> {
                val showErrorUiOperation = uiOperation as ShowErrorUiOperation

                onErrorOccurred(showErrorUiOperation.error)
            }
        }
    }

    protected open fun processAuthorizeUiOperation(authorizeUiOperation: AuthorizeUiOperation) {
        val authUiState = mModel.authUiState.value!!

        if (!authUiState.isAuthorized) return

        moveToMainMenu()
    }

    protected abstract fun moveToMainMenu()
}