package com.qubacy.geoqq.ui.screen.geochat.auth.common

import android.os.Bundle
import android.view.View
import com.qubacy.geoqq.ui.common.fragment.waiting.WaitingFragment
import com.qubacy.geoqq.ui.screen.geochat.auth.common.model.AuthViewModel

abstract class AuthFragment : WaitingFragment() {
    abstract override val mModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mModel.accessToken.observe(viewLifecycleOwner) {
            onAccessTokenGotten(it)
        }
    }

    protected fun onAccessTokenGotten(accessToken: String) {
        // todo: is there a need to check the token for validity here?

        mMainModel.setAccessToken(accessToken)
    }
}