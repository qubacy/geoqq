package com.qubacy.geoqq.data._common.repository

abstract class DataRepositoryTest<DataRepositoryType : Any> {
    protected lateinit var mDataRepository: DataRepositoryType

}