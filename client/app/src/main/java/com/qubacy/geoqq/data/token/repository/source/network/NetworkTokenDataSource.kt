package com.qubacy.geoqq.data.token.repository.source.network

import com.qubacy.geoqq.data.common.repository.source.DataSource
import com.qubacy.geoqq.data.token.repository.source.network.model.response.UpdateTokensResponse
import retrofit2.Call

interface NetworkTokenDataSource : DataSource {
//    @GET("/")
    fun updateTokens(refreshToken: String): Call<UpdateTokensResponse>
}

