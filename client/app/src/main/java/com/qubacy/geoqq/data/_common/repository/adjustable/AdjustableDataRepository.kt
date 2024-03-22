package com.qubacy.geoqq.data._common.repository.adjustable

import com.qubacy.geoqq._common.coroutine.CoroutineUser
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class AdjustableDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : CoroutineUser(coroutineDispatcher, coroutineScope), DataRepository {

}