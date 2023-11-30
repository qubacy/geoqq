package com.qubacy.geoqq.data.common.repository.source.local

import androidx.test.core.app.ApplicationProvider
import com.qubacy.geoqq.applicaion.common.Application
import com.qubacy.geoqq.applicaion.common.container.AppContainer
import com.qubacy.geoqq.applicaion.common.container.AppContainerImpl
import com.qubacy.geoqq.application.TestApplication
import com.qubacy.geoqq.common.ApplicationTestBase
import com.qubacy.geoqq.data.common.repository.common.source.local.database.Database
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible


abstract class DatabaseSourceTest(

) : ApplicationTestBase() {
    protected lateinit var mDatabase: Database

    override fun setup() {
        super.setup()

        val app = ApplicationProvider.getApplicationContext<Application>() as Application
        val databaseFieldReflection = AppContainer::class.declaredMemberProperties.find { it.name == "mDatabase"}!!
        mDatabase = databaseFieldReflection.get(app.appContainer) as Database
    }
}