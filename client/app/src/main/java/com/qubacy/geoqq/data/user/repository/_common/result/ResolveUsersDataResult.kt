package com.qubacy.geoqq.data.user.repository._common.result

import com.qubacy.geoqq.data._common.repository.producing.result.ProducingDataResult
import com.qubacy.geoqq.data.user.model.DataUser

class ResolveUsersDataResult(
    isNewest: Boolean,
    val userIdUserMap: Map<Long, DataUser>
) : ProducingDataResult(isNewest) {

}