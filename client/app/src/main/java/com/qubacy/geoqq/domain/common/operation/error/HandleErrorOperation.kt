package com.qubacy.geoqq.domain.common.operation.error

import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.domain.common.operation.common.Operation

class HandleErrorOperation(
    val error: Error
) : Operation() {

}