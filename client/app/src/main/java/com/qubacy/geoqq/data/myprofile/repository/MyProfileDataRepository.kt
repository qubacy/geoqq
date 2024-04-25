package com.qubacy.geoqq.data.myprofile.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.model.DataImage
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.myprofile.model.profile.DataMyProfile
import com.qubacy.geoqq.data.myprofile.model.update.DataMyProfileUpdateData
import com.qubacy.geoqq.data.myprofile.model.profile.toDataMyProfile
import com.qubacy.geoqq.data.myprofile.model.profile.toMyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.model.update.toMyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.model.update.toUpdateMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileDataResult
import com.qubacy.geoqq.data.myprofile.repository.source.http.HttpMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.http.request.DeleteMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository.source.http.response.GetMyProfileResponse
import com.qubacy.geoqq.data.myprofile.repository.source.local.LocalMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.MyProfileDataStoreModel
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class MyProfileDataRepository @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorDataRepository: ErrorDataRepository,
    private val mTokenDataRepository: TokenDataRepository,
    private val mImageDataRepository: ImageDataRepository,
    private val mLocalMyProfileDataSource: LocalMyProfileDataSource,
    private val mHttpMyProfileDataSource: HttpMyProfileDataSource,
    private val mHttpCallExecutor: HttpCallExecutor
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    suspend fun getMyProfile(): LiveData<GetMyProfileDataResult> {
        val resultLiveData = MutableLiveData<GetMyProfileDataResult>()

        CoroutineScope(coroutineContext).launch {
            val localMyProfile = mLocalMyProfileDataSource.getMyProfile()

            if (localMyProfile != null) {
                val localDataMyProfile = resolveMyProfileDataStoreModel(localMyProfile)

                resultLiveData.postValue(GetMyProfileDataResult(localDataMyProfile))
            }

            val accessToken = mTokenDataRepository.getTokens().accessToken

            val myProfileCall = mHttpMyProfileDataSource.getMyProfile(accessToken)
            val myProfileResponse = mHttpCallExecutor.executeNetworkRequest(myProfileCall)

            val httpDataMyProfile = resolveGetMyProfileResponse(myProfileResponse)

            if (localMyProfile == null)
                resultLiveData.postValue(GetMyProfileDataResult(httpDataMyProfile))
            else mResultFlow.emit(GetMyProfileDataResult(httpDataMyProfile))

            val myProfileToSave = httpDataMyProfile.toMyProfileDataStoreModel()

            mLocalMyProfileDataSource.saveMyProfile(myProfileToSave)
        }

        return resultLiveData
    }

    suspend fun updateMyProfile(updateProfileData: DataMyProfileUpdateData) {
        val accessToken = mTokenDataRepository.getTokens().accessToken

        var avatar: DataImage? = null

        if (updateProfileData.avatarUri != null)
            avatar = mImageDataRepository.saveImage(updateProfileData.avatarUri)

        val updateMyProfileRequest = updateProfileData
            .toUpdateMyProfileRequest(accessToken, avatar?.id)
        val updateMyProfileCall = mHttpMyProfileDataSource.updateMyProfile(updateMyProfileRequest)
        val updateMyProfileResponse = mHttpCallExecutor.executeNetworkRequest(updateMyProfileCall)

        val myProfileToSave = getUpdatedMyProfileToSave(updateProfileData)

        mLocalMyProfileDataSource.saveMyProfile(myProfileToSave)
    }

    suspend fun deleteMyProfile() {
        val accessToken = mTokenDataRepository.getTokens().accessToken

        val deleteMyProfileRequest = DeleteMyProfileRequest(accessToken)
        val deleteMyProfileCall = mHttpMyProfileDataSource.deleteMyProfile(deleteMyProfileRequest)
        val deleteMyProfileResponse = mHttpCallExecutor.executeNetworkRequest(deleteMyProfileCall)

        mLocalMyProfileDataSource.resetMyProfile()
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
        val myProfileDataStoreModel = mLocalMyProfileDataSource.getMyProfile()!!

        return updateProfileData.toMyProfileDataStoreModel(myProfileDataStoreModel, avatar)
    }
}