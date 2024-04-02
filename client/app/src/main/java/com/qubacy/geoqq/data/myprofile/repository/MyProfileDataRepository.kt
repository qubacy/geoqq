package com.qubacy.geoqq.data.myprofile.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data._common.util.http.executor.executeNetworkRequest
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.image.repository.ImageDataRepository
import com.qubacy.geoqq.data.myprofile.model.DataMyProfile
import com.qubacy.geoqq.data.myprofile.model.toDataMyProfile
import com.qubacy.geoqq.data.myprofile.model.toMyProfileDataStoreModel
import com.qubacy.geoqq.data.myprofile.model.toUpdateMyProfileRequest
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileResult
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
    private val mHttpMyProfileDataSource: HttpMyProfileDataSource
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    suspend fun getMyProfile(): LiveData<GetMyProfileResult> {
        val resultLiveData = MutableLiveData<GetMyProfileResult>()

        CoroutineScope(coroutineContext).launch {
            val localMyProfile = mLocalMyProfileDataSource.getMyProfile()

            if (localMyProfile != null) {
                val localDataMyProfile = resolveMyProfileDataStoreModel(localMyProfile)

                resultLiveData.postValue(GetMyProfileResult(localDataMyProfile))
            }

            val accessToken = mTokenDataRepository.getTokens().accessToken

            val myProfileCall = mHttpMyProfileDataSource.getMyProfile(accessToken)
            val myProfileResponse = executeNetworkRequest(mErrorDataRepository, myProfileCall)

            val httpDataMyProfile = resolveGetMyProfileResponse(myProfileResponse)

            if (localMyProfile == null)
                resultLiveData.postValue(GetMyProfileResult(httpDataMyProfile))
            else mResultFlow.emit(GetMyProfileResult(httpDataMyProfile))

            val myProfileToSave = httpDataMyProfile.toMyProfileDataStoreModel()

            mLocalMyProfileDataSource.saveMyProfile(myProfileToSave)
        }

        return resultLiveData
    }

    suspend fun updateMyProfile(
        myProfile: DataMyProfile,
        password: String,
        newPassword: String
    ) {
        val accessToken = mTokenDataRepository.getTokens().accessToken

        val updateMyProfileRequest = myProfile.toUpdateMyProfileRequest(
            accessToken, password, newPassword)
        val updateMyProfileCall = mHttpMyProfileDataSource.updateMyProfile(updateMyProfileRequest)
        val updateMyProfileResponse = executeNetworkRequest(
            mErrorDataRepository, updateMyProfileCall)

        val myProfileToSave = myProfile.toMyProfileDataStoreModel()

        mLocalMyProfileDataSource.saveMyProfile(myProfileToSave)
    }

    suspend fun deleteMyProfile() {
        val accessToken = mTokenDataRepository.getTokens().accessToken

        val deleteMyProfileRequest = DeleteMyProfileRequest(accessToken)
        val deleteMyProfileCall = mHttpMyProfileDataSource.deleteMyProfile(deleteMyProfileRequest)
        val deleteMyProfileResponse = executeNetworkRequest(
            mErrorDataRepository, deleteMyProfileCall)

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
}