package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._di.module

import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest._common.RemoteMateMessageHttpRestDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.rest.impl.RemoteMateMessageHttpRestDataSourceImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteMateMessageHttpRestDataSourceModule {
    @Binds
    abstract fun bindRemoteMateMessageHttpRestDataSource(
        remoteMateMessageHttpRestDataSource: RemoteMateMessageHttpRestDataSourceImpl
    ): RemoteMateMessageHttpRestDataSource
}