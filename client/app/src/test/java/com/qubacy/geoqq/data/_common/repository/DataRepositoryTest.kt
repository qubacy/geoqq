package com.qubacy.geoqq.data._common.repository

import com.qubacy.geoqq.data._common.repository._common.DataRepository

abstract class DataRepositoryTest<DataRepositoryType : DataRepository> {
    protected lateinit var mDataRepository: DataRepositoryType


}