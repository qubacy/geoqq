package com.qubacy.geoqq.data.myprofile.repository

import android.graphics.Bitmap
import com.qubacy.geoqq.data.common.repository.common.result.common.Result
import com.qubacy.geoqq.data.common.repository.common.result.error.ErrorResult
import com.qubacy.geoqq.data.common.repository.common.result.interruption.InterruptionResult
import com.qubacy.geoqq.data.common.repository.common.source.network.model.response.common.Response
import com.qubacy.geoqq.data.common.repository.network.common.result.ExecuteNetworkRequestResult
import com.qubacy.geoqq.data.common.repository.network.flowable.FlowableDataRepository
import com.qubacy.geoqq.data.common.util.HasherUtil
import com.qubacy.geoqq.data.common.util.StringEncodingDecodingUtil
import com.qubacy.geoqq.data.myprofile.model.common.MyProfileDataModelContext
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileResult
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileWithNetworkResult
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileWithSharedPreferencesResult
import com.qubacy.geoqq.data.myprofile.repository.result.UpdateMyProfileResult
import com.qubacy.geoqq.data.myprofile.repository.source.local.LocalMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.local.model.toDataMyProfile
import com.qubacy.geoqq.data.myprofile.repository.source.network.NetworkMyProfileDataSource
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.common.Privacy
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.request.UpdateMyProfileRequestBody
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.request.common.Security
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.response.GetMyProfileResponse
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.response.UpdateMyProfileResponse
import com.qubacy.geoqq.data.myprofile.repository.source.network.model.response.toDataMyProfile
import java.nio.ByteBuffer

class MyProfileDataRepository(
    val localMyProfileDataSource: LocalMyProfileDataSource,
    val networkMyProfileDataSource: NetworkMyProfileDataSource
) : FlowableDataRepository() {
    private fun getMyProfileWithSharedPreferences(): Result {
        val myProfileData = localMyProfileDataSource.loadMyProfileData()

        if (myProfileData == null)
            return GetMyProfileWithSharedPreferencesResult(null)

        return GetMyProfileWithSharedPreferencesResult(myProfileData.toDataMyProfile())
    }

    private fun getMyProfileWithNetwork(accessToken: String): Result {
        val request = networkMyProfileDataSource.getMyProfile(accessToken) as retrofit2.Call<Response>
        val result = executeNetworkRequest(request)

        if (result is ErrorResult) return result
        if (result is InterruptionResult) return result

        val responseBody = (result as ExecuteNetworkRequestResult).response as GetMyProfileResponse

        return GetMyProfileWithNetworkResult(responseBody.toDataMyProfile())
    }

    suspend fun getMyProfile(accessToken: String) {
        val getMyProfileWithSharedPreferencesResult = getMyProfileWithSharedPreferences()

        if (getMyProfileWithSharedPreferencesResult is ErrorResult)
            return emitResult(getMyProfileWithSharedPreferencesResult)

        val getMyProfileWithSharedPreferencesResultCast =
            getMyProfileWithSharedPreferencesResult as GetMyProfileWithSharedPreferencesResult

        if (getMyProfileWithSharedPreferencesResultCast.myProfileData != null)
            emitResult(GetMyProfileResult(getMyProfileWithSharedPreferencesResultCast.myProfileData))

        val getMyProfileWithNetworkResult = getMyProfileWithNetwork(accessToken)

        if (getMyProfileWithNetworkResult is ErrorResult)
            return emitResult(getMyProfileWithNetworkResult)
        if (getMyProfileWithNetworkResult is InterruptionResult)
            return emitResult(getMyProfileWithNetworkResult)

        val getMyProfileWithNetworkResultCast =
            getMyProfileWithNetworkResult as GetMyProfileWithNetworkResult

        return emitResult(GetMyProfileResult(getMyProfileWithNetworkResultCast.myProfileData))
    }

    suspend fun updateMyProfile(
        accessToken: String,
        avatarBitmap: Bitmap?,
        description: String?,
        password: String?,
        newPassword: String?,
        hitUpOption: MyProfileDataModelContext.HitUpOption?
    ) {
        val avatarContent = if (avatarBitmap == null) null
            else {
                val avatarByteBuffer = ByteBuffer.allocate(
                    avatarBitmap.rowBytes * avatarBitmap.height)

                avatarBitmap.copyPixelsToBuffer(avatarByteBuffer)

                StringEncodingDecodingUtil.bytesAsBase64String(avatarByteBuffer.array())
            }
        val passwordHash = if (password == null) null
            else {
                val passwordHashBytes =
                    HasherUtil.hashString(password, HasherUtil.HashAlgorithm.SHA256)

                StringEncodingDecodingUtil.bytesAsBase64String(passwordHashBytes)
            }
        val newPasswordHash = if (newPassword == null) null
            else {
                val newPasswordHashBytes =
                    HasherUtil.hashString(newPassword, HasherUtil.HashAlgorithm.SHA256)

                StringEncodingDecodingUtil.bytesAsBase64String(newPasswordHashBytes)
            }
        val privacy = if (hitUpOption == null) null
            else {
                Privacy(hitUpOption.index)
            }
        val security = if (passwordHash == null || newPasswordHash == null) null
            else {
                Security(passwordHash, newPasswordHash)
            }

        val requestBody = UpdateMyProfileRequestBody(
            accessToken,
            description,
            avatarContent,
            privacy,
            security
        )

        val request = networkMyProfileDataSource.updateMyProfile(requestBody) as retrofit2.Call<Response>
        val result = executeNetworkRequest(request)

        if (result is ErrorResult) return emitResult(result)
        if (result is InterruptionResult) return emitResult(result)

        val responseBody = (result as ExecuteNetworkRequestResult).response as UpdateMyProfileResponse

        return emitResult(UpdateMyProfileResult())
    }
}