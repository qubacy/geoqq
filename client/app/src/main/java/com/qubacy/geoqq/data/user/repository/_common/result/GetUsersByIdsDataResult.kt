package com.qubacy.geoqq.data.user.repository._common.result

import com.qubacy.geoqq.data._common.repository.producing.result.ProducingDataResult
import com.qubacy.geoqq.data.user.model.DataUser

class GetUsersByIdsDataResult(
    isNewest: Boolean,
    val users: List<DataUser>
) : ProducingDataResult(isNewest) {

}