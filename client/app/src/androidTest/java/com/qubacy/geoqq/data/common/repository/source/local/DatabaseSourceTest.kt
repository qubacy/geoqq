package com.qubacy.geoqq.data.common.repository.source.local

import androidx.test.core.app.ApplicationProvider
import com.qubacy.geoqq.applicaion.common.Application
import com.qubacy.geoqq.applicaion.common.container.AppContainerImpl
import com.qubacy.geoqq.data.common.repository.common.source.local.database.Database


abstract class DatabaseSourceTest(

) {
    protected val mDatabase: Database

    init {
        val app = ApplicationProvider.getApplicationContext<Application>() as Application
        val databaseFieldReflection = AppContainerImpl::class.java.getDeclaredField("database")
            .apply { isAccessible = true }
        mDatabase = databaseFieldReflection.get(app.appContainer) as Database
    }
}