package com.qubacy.geoqq.domain.myprofile.state

import android.net.Uri
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.state.common.State
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext

class MyProfileState(
    val avatar: Uri = Uri.parse(String()),
    val username: String = String(),
    val description: String = String(),
    val hitUpOption: MyProfileDataModelContext.HitUpOption = MyProfileDataModelContext.HitUpOption.POSITIVE,
    newOperations: List<Operation> = listOf()
) : State(newOperations) {

}