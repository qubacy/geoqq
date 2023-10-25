package com.qubacy.geoqq.ui.screen.myprofile.model.state

import android.net.Uri
import com.qubacy.geoqq.data.myprofile.MyProfileContext
import com.qubacy.geoqq.ui.common.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.fragment.common.base.model.state.OperationUiState

class MyProfileUiState(
    val avatar: Uri? = null,
    val username: String? = null,
    val description: String? = null,
    val password: String? = null,
    val hitUpOption: MyProfileContext.HitUpOption? = null,
    newOperations: List<UiOperation>
) : OperationUiState(newOperations) {
    fun isFull(): Boolean {
        return (username != null && description != null && hitUpOption != null)
    }
}