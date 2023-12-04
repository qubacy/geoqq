package com.qubacy.geoqq.domain.myprofile.state

import android.net.Uri
import com.qubacy.geoqq.data.myprofile.model.common.DataMyProfile
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.state.common.State

class MyProfileState(
    val avatar: Uri = Uri.parse(String()),
    val username: String = String(),
    val description: String = String(),
    val hitUpOption: DataMyProfile.HitUpOption = DataMyProfile.HitUpOption.POSITIVE,
    newOperations: List<Operation> = listOf()
) : State(newOperations) {

}