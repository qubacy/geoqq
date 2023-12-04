package com.qubacy.geoqq.ui.screen.myprofile.model.state

import android.net.Uri
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.model.state.OperationUiState

class MyProfileUiState(
    val avatar: Uri,
    val username: String,
    val description: String,
    val hitUpOption: DataMyProfile.HitUpOption,
    newOperations: List<UiOperation>
) : OperationUiState(newOperations) {
    fun isFull(): Boolean { // todo: rethink this..
        return (username.isNotEmpty())
    }
}