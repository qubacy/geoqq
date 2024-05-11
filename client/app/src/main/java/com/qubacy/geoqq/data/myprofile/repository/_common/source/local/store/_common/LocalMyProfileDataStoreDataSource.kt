package com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.model.MyProfileDataStoreModel

val Context.myProfileDataStore: DataStore<Preferences> by preferencesDataStore(
    LocalMyProfileDataStoreDataSource.DATA_STORE_NAME
)

interface LocalMyProfileDataStoreDataSource {
    companion object {
        const val DATA_STORE_NAME = "myProfile"
    }

    suspend fun getMyProfile(): MyProfileDataStoreModel?
    suspend fun saveMyProfile(myProfile: MyProfileDataStoreModel)
    suspend fun resetMyProfile()
}