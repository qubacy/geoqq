package com.qubacy.geoqq.data.myprofile.repository.source.local

import android.content.SharedPreferences
import com.qubacy.geoqq.data.common.repository.common.source.DataSource
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.MyProfileEntity

class LocalMyProfileDataSource(
    private val mMyProfileSharedPreferences: SharedPreferences
) : DataSource {
    companion object {
        const val MY_PROFILE_SHARED_PREFERENCES_NAME = "myProfile"

        const val AVATAR_URI_PREFERENCE_KEY = "avatarUri"
        const val USERNAME_PREFERENCE_KEY = "username"
        const val DESCRIPTION_PREFERENCE_KEY = "description"
        const val HIT_UP_OPTION_INDEX_PREFERENCE_KEY = "hitUpOption"

        const val INCORRECT_HIT_UP_OPTION_INDEX_VALUE = -1
    }

    private var mMyProfileEntity: MyProfileEntity? = null

    fun saveMyProfileData(myProfileData: MyProfileEntity) {
        mMyProfileSharedPreferences.edit()
            .putString(AVATAR_URI_PREFERENCE_KEY, myProfileData.avatarUri)
            .putString(USERNAME_PREFERENCE_KEY, myProfileData.username)
            .putString(DESCRIPTION_PREFERENCE_KEY, myProfileData.description)
            .putInt(HIT_UP_OPTION_INDEX_PREFERENCE_KEY, myProfileData.hitUpOptionIndex)
            .commit()

        mMyProfileEntity = myProfileData
    }

    fun loadMyProfileData(): MyProfileEntity? {
        if (mMyProfileEntity != null) return mMyProfileEntity!!

        val avatarUri =
            mMyProfileSharedPreferences.getString(AVATAR_URI_PREFERENCE_KEY, null)
        val username =
            mMyProfileSharedPreferences.getString(USERNAME_PREFERENCE_KEY, null)
        val description =
            mMyProfileSharedPreferences.getString(DESCRIPTION_PREFERENCE_KEY, null)
        val hitUpOptionIndex = mMyProfileSharedPreferences.getInt(
            HIT_UP_OPTION_INDEX_PREFERENCE_KEY, INCORRECT_HIT_UP_OPTION_INDEX_VALUE)

        if (avatarUri == null || username == null || description == null
            || hitUpOptionIndex == INCORRECT_HIT_UP_OPTION_INDEX_VALUE)
        {
            return null
        }

        mMyProfileEntity = MyProfileEntity(avatarUri, username, description, hitUpOptionIndex)

        return mMyProfileEntity
    }
}