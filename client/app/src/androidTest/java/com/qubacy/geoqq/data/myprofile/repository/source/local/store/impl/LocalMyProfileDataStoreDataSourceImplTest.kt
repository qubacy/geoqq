package com.qubacy.geoqq.data.myprofile.repository.source.local.store.impl

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store.impl.LocalMyProfileDataStoreDataSourceImpl
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.myProfileDataStore
import com.qubacy.geoqq.data.myprofile.repository.source.local.store._common._test.context.LocalMyProfileDataStoreDataSourceTestContext
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMyProfileDataStoreDataSourceImplTest() {
    companion object {
        val DEFAULT_MY_PROFILE_DATA_STORE_MODEL = LocalMyProfileDataStoreDataSourceTestContext
            .DEFAULT_MY_PROFILE_DATA_STORE_MODEL
    }

    private lateinit var mMyProfileDataSource: LocalMyProfileDataStoreDataSourceImpl

    @Before
    fun setup() {
        initMyProfileDataSource()
    }

    private fun initMyProfileDataSource() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        mMyProfileDataSource = LocalMyProfileDataStoreDataSourceImpl(context.myProfileDataStore)
    }

    @Test
    fun saveMyProfileThenGetItTest() = runTest {
        val expectedMyProfileDataStoreModel = DEFAULT_MY_PROFILE_DATA_STORE_MODEL

        mMyProfileDataSource.saveMyProfile(expectedMyProfileDataStoreModel)

        val gottenMyProfileDataStoreModel = mMyProfileDataSource.getMyProfile()

        Assert.assertEquals(expectedMyProfileDataStoreModel, gottenMyProfileDataStoreModel)
    }

    @Test
    fun resetMyProfileTest() = runTest {
        val initMyProfileDataStoreModel = DEFAULT_MY_PROFILE_DATA_STORE_MODEL

        mMyProfileDataSource.saveMyProfile(initMyProfileDataStoreModel)
        mMyProfileDataSource.resetMyProfile()

        val gottenMyProfileDataStoreModel = mMyProfileDataSource.getMyProfile()

        Assert.assertNull(gottenMyProfileDataStoreModel)
    }
}