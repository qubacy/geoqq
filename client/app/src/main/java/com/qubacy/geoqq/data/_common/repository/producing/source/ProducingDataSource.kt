package com.qubacy.geoqq.data._common.repository.producing.source

interface ProducingDataSource {
    fun startProducing()
    fun stopProducing()
    fun reset() {}
}