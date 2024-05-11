package com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common

import com.qubacy.geoqq.data.user.repository._common.source.remote.http.rest._common.api.response.GetUsersResponse

interface RemoteUserHttpRestDataSource {
    fun getUsers(ids: List<Long>): GetUsersResponse
}