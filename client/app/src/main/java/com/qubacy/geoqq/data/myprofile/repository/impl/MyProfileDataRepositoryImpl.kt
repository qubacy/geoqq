package com.qubacy.geoqq.data.myprofile.repository.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.image.repository._common.ImageDataRepository
import com.qubacy.geoqq.data.myprofile.model.profile.DataMyProfile
import com.qubacy.geoqq.data.myprofile.model.update.DataMyProfileUpdateData
import com.qubacy.geoqq.data.myprofile.model.profile.toDataMyProfile
import com.qubacy.geoqq.data.myprofile.model.profile.toMyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.model.update.toMyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.model.update.toUpdateMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository._common.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository._common.result.GetMyProfileDataResult
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.LocalMyProfileDataStoreDataSource
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.api.response.GetMyProfileResponse
import com.qubacy.geoqq.data.myprofile.repository._common.source.local.store._common.model.MyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.repository._common.source.remote.http.rest._common.RemoteMyProfileHttpRestDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class MyProfileDataRepositoryImpl @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mImageDataRepository: ImageDataRepository,
    private val mLocalMyProfileDataStoreDataSource: LocalMyProfileDataStoreDataSource,
    private val mRemoteMyProfileHttpRestDataSource: RemoteMyProfileHttpRestDataSource
) : MyProfileDataRepository(coroutineDispatcher, coroutineScope) {
    override suspend fun getMyProfile(): LiveData<GetMyProfileDataResult> {
        val resultLiveData = MutableLiveData<GetMyProfileDataResult>()

        CoroutineScope(coroutineContext).launch {
            val localMyProfile = mLocalMyProfileDataStoreDataSource.getMyProfile()
            val localDataMyProfile = localMyProfile?.let {
                resolveMyProfileDataStoreModel(localMyProfile) }

            if (localDataMyProfile != null)
                resultLiveData.postValue(GetMyProfileDataResult(false, localDataMyProfile))

            val myProfileResponse = mRemoteMyProfileHttpRestDataSource.getMyProfile()

            val httpDataMyProfile = resolveGetMyProfileResponse(myProfileResponse)

            resultLiveData.postValue(GetMyProfileDataResult(true, httpDataMyProfile))

            if (localDataMyProfile == httpDataMyProfile) return@launch

            val myProfileToSave = httpDataMyProfile.toMyProfileDataStoreModel()

            mLocalMyProfileDataStoreDataSource.saveMyProfile(myProfileToSave)
        }

        return resultLiveData
    }

    override suspend fun updateMyProfile(updateProfileData: DataMyProfileUpdateData) {
        var avatar: DataImage? = null

        if (updateProfileData.avatarUri != null)
            avatar = mImageDataRepository.saveImage(updateProfileData.avatarUri)

        val updateMyProfileRequest = updateProfileData.toUpdateMyProfileRequest(avatar?.id)

        mRemoteMyProfileHttpRestDataSource.updateMyProfile(updateMyProfileRequest)

        val myProfileToSave = getUpdatedMyProfileToSave(updateProfileData)

        mLocalMyProfileDataStoreDataSource.saveMyProfile(myProfileToSave)
    }

    override suspend fun deleteMyProfile() {
        mRemoteMyProfileHttpRestDataSource.deleteMyProfile()
        mLocalMyProfileDataStoreDataSource.resetMyProfile()
    }

    private suspend fun resolveMyProfileDataStoreModel(
        myProfileDataStoreModel: MyProfileDataStoreModel
    ): DataMyProfile {
        val avatar = mImageDataRepository.getImageById(myProfileDataStoreModel.avatarId)

        return myProfileDataStoreModel.toDataMyProfile(avatar)
    }

    private suspend fun resolveGetMyProfileResponse(
        getMyProfileResponse: GetMyProfileResponse
    ): DataMyProfile {
        val avatar = mImageDataRepository.getImageById(getMyProfileResponse.avatarId)

        return getMyProfileResponse.toDataMyProfile(avatar)
    }

    private suspend fun getUpdatedMyProfileToSave(
        updateProfileData: DataMyProfileUpdateData,
        avatar: DataImage? = null
    ): MyProfileDataStoreModel {
        val myProfileDataStoreModel = mLocalMyProfileDataStoreDataSource.getMyProfile()!!

        return updateProfileData.toMyProfileDataStoreModel(myProfileDataStoreModel, avatar)
    }
}