package com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common._test.context

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.dao.model.ErrorEntity

object LocalErrorDatabaseDataSourceTestContext {
    const val DEFAULT_TEST_ERROR_ID = 20000000L
    val DEFAULT_ERROR_ENTITY = ErrorEntity(
        DEFAULT_TEST_ERROR_ID, "en", "test error", false)
}