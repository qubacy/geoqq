package com.qubacy.geoqq.ui.application.activity._common.screen.login.model._common.state

import com.qubacy.geoqq._common.model.error._common.Error
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.model.state.BusinessUiState

open class LoginUiState(
    isLoading: Boolean = false,
    error: Error? = null,
    var loginMode: LoginMode = LoginMode.SIGN_IN,
    var autoSignInAllowed: Boolean = true
) : BusinessUiState(isLoading, error) {
    enum class LoginMode(val id: Int) {
        SIGN_IN(0), SIGN_UP(1);

        companion object {
            fun getLoginModeById(id: Int): LoginMode {
                return entries.find { it.id == id }!!
            }

            fun getNextLoginMode(loginMode: LoginMode): LoginMode {
                val nextLoginModeId = (loginMode.id + 1) % entries.size

                return getLoginModeById(nextLoginModeId)
            }
        }
    }

    override fun copy(): LoginUiState {
        return LoginUiState(isLoading, error?.copy(), loginMode, autoSignInAllowed)
    }
}