package com.qubacy.geoqq.ui.screen.myprofile.model.state

import android.net.Uri
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.ui.common.fragment.common.model.state.BaseUiState
import com.qubacy.geoqq.ui.screen.myprofile.model.MyProfileViewModel

class MyProfileUiState(
    val avatar: Uri? = null,
    val username: String? = null,
    val description: String? = null,
    val password: String? = null,
    val hitUpOption: MyProfileViewModel.HitUpOption? = null,
    error: Error? = null
) : BaseUiState(error) {

}