package com.qubacy.geoqq.data.common.repository.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

abstract class DataRepository(
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    abstract fun interrupt()
}