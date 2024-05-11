package com.qubacy.geoqq.data.myprofile.repository.source.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq._common.model.hitmeup.HitMeUpType
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store.impl.LocalMyProfileDataStoreDataSourceImpl
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.model.MyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.repository.source.local.store.myProfileDataStore
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMyProfileDataSourceTest() {
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
        val expectedMyProfileDataStoreModel = MyProfileDataStoreModel(
            0, "test", "test", "test", HitMeUpType.EVERYBODY.id
        )

        mMyProfileDataSource.saveMyProfile(expectedMyProfileDataStoreModel)

        val gottenMyProfileDataStoreModel = mMyProfileDataSource.getMyProfile()

        Assert.assertEquals(expectedMyProfileDataStoreModel, gottenMyProfileDataStoreModel)
    }

    @Test
    fun resetMyProfileTest() = runTest {
        val initMyProfileDataStoreModel = MyProfileDataStoreModel(
            0, "test", "test", "test", HitMeUpType.EVERYBODY.id
        )

        mMyProfileDataSource.saveMyProfile(initMyProfileDataStoreModel)
        mMyProfileDataSource.resetMyProfile()

        val gottenMyProfileDataStoreModel = mMyProfileDataSource.getMyProfile()

        Assert.assertNull(gottenMyProfileDataStoreModel)
    }
}