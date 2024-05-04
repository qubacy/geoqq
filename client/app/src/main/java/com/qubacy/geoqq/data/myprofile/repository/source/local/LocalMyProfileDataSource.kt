package com.qubacy.geoqq.data.myprofile.repository.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.qubacy.geoqq.data._common.repository._common.source._common.DataSource
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.MyProfileDataStoreModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

val Context.myProfileDataStore: DataStore<Preferences> by preferencesDataStore(
    LocalMyProfileDataSource.DATA_STORE_NAME
)

class LocalMyProfileDataSource @Inject constructor(
    private val mMyProfileDataStore: DataStore<Preferences>
) : DataSource {
    companion object {
        const val DATA_STORE_NAME = "myProfile"

        val AVATAR_ID_KEY = longPreferencesKey("avatarId")
        val LOGIN_KEY = stringPreferencesKey("login")
        val USERNAME_KEY = stringPreferencesKey("username")
        val ABOUT_ME_KEY = stringPreferencesKey("aboutMe")
        val HIT_ME_UP_ID_KEY = intPreferencesKey("hitMeUpId")
    }

    suspend fun getMyProfile(): MyProfileDataStoreModel? {
        val preferences = mMyProfileDataStore.data.first()

        val avatarId = preferences[AVATAR_ID_KEY] ?: return null
        val login = preferences[LOGIN_KEY] ?: return null
        val username = preferences[USERNAME_KEY] ?: return null
        val aboutMe = preferences[ABOUT_ME_KEY] ?: return null
        val hitMeUpId = preferences[HIT_ME_UP_ID_KEY] ?: return null

        return MyProfileDataStoreModel(avatarId, login, username, aboutMe, hitMeUpId)
    }

    suspend fun saveMyProfile(myProfile: MyProfileDataStoreModel) {
        mMyProfileDataStore.edit {
            it[AVATAR_ID_KEY] = myProfile.avatarId
            it[LOGIN_KEY] = myProfile.login
            it[USERNAME_KEY] = myProfile.username
            it[ABOUT_ME_KEY] = myProfile.aboutMe
            it[HIT_ME_UP_ID_KEY] = myProfile.hitMeUpId
        }
    }

    suspend fun resetMyProfile() {
        mMyProfileDataStore.edit {
            it.clear()
        }
    }
}