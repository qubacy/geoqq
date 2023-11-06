package com.qubacy.geoqq.data.common.repository.network.updatable.source.update

import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import com.qubacy.geoqq.data.common.repository.network.updatable.source.update.update.Update
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

abstract class UpdateDataSource(

) : DataSource {
    protected val mUpdateFlow = MutableSharedFlow<List<Update>>()
    val updateFlow: SharedFlow<List<Update>> = mUpdateFlow

    abstract fun stopUpdateListening()
    abstract fun startUpdateListening()
}