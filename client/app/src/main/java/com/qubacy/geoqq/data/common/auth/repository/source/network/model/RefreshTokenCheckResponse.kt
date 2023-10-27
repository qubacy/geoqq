package com.qubacy.geoqq.data.common.auth.repository.source.network.model

import com.qubacy.geoqq.common.repository.source.network.model.Response
import com.qubacy.geoqq.common.repository.source.network.model.ServerError

class RefreshTokenCheckResponse(
    error: ServerError
) : Response(error) {

}