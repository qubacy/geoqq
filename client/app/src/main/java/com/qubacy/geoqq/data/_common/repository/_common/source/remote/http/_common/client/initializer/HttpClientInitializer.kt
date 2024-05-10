package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.client.initializer

import okhttp3.OkHttpClient

interface HttpClientInitializer {
    fun initializeHttpClient(httpClientBuilder: OkHttpClient.Builder)
}