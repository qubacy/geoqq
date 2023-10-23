package com.qubacy.geoqq.data.myprofile.state

import android.net.Uri
import com.qubacy.geoqq.data.common.operation.Operation
import com.qubacy.geoqq.data.common.state.State
import com.qubacy.geoqq.data.myprofile.MyProfileContext

class MyProfileState(
    val avatar: Uri? = null,
    val username: String? = null,
    val description: String? = null,
    val password: String? = null,
    val hitUpOption: MyProfileContext.HitUpOption? = null,
    newOperations: List<Operation> = listOf()
) : State(newOperations) {

}