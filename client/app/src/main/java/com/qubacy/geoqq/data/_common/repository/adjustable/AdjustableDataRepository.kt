package com.qubacy.geoqq.data._common.repository.adjustable

import com.qubacy.geoqq._common.coroutine.CoroutineUser
import com.qubacy.geoqq.data._common.repository._common.DataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

abstract class AdjustableDataRepository(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher
) : CoroutineUser(coroutineDispatcher, coroutineScope), DataRepository {

}