package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor.impl.HttpCallExecutorImpl
import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.executor._common.HttpCallExecutor
import dagger.Binds
import dagger.Module

@Module
abstract class HttpCallExecutorModule {
    @Binds
    abstract fun bindHttpCallExecutor(
        httpCallExecutor: HttpCallExecutorImpl
    ): HttpCallExecutor
}