package com.qubacy.geoqq.data.myprofile.repository.source.local

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.MyProfileEntity
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalMyProfileDataSourceTest() {
    private lateinit var mMyProfileSharedPreferences: SharedPreferences

    @Before
    fun setup() {
        mMyProfileSharedPreferences = InstrumentationRegistry.getInstrumentation()
            .targetContext.getSharedPreferences(
                LocalMyProfileDataSource.MY_PROFILE_SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Test
    fun saveMyProfileDataThenLoadTest() {
        var localMyProfileDataSource = LocalMyProfileDataSource(mMyProfileSharedPreferences)

        val myProfileEntity = MyProfileEntity(
            String(), "test", "test", 0)

        localMyProfileDataSource.saveMyProfileData(myProfileEntity)

        localMyProfileDataSource = LocalMyProfileDataSource(mMyProfileSharedPreferences)

        val loadedMyProfileEntity = localMyProfileDataSource.loadMyProfileData()

        Assert.assertNotNull(loadedMyProfileEntity)
        Assert.assertEquals(myProfileEntity, loadedMyProfileEntity)
    }
}