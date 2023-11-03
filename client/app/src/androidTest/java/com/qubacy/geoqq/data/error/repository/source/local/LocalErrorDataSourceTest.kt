package com.qubacy.geoqq.data.error.repository.source.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.data.common.repository.source.local.DatabaseSourceTest
import com.qubacy.geoqq.data.error.repository.source.local.model.ErrorEntity
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalErrorDataSourceTest() : DatabaseSourceTest() {
    private lateinit var mLocalErrorDataSource: LocalErrorDataSource

    @Before
    fun setup() {
        mLocalErrorDataSource = mDatabase.getErrorDAO()
    }

    @Test
    fun getErrorByIdTest() {
        val expectedErrorEntity =
            ErrorEntity(1, "Unknown database error!", "en", 1)

        val errorEntity = mLocalErrorDataSource.getErrorById(
            expectedErrorEntity.id, expectedErrorEntity.lang)

        Assert.assertEquals(expectedErrorEntity, errorEntity)
    }
}