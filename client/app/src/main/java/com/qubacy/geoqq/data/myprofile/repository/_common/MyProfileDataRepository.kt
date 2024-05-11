package com.qubacy.geoqq.data.myprofile.repository._common

import androidx.lifecycle.LiveData
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.myprofile.model.update.DataMyProfileUpdateData
import com.qubacy.geoqq.data.myprofile.repository._common.result.GetMyProfileDataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class MyProfileDataRepository(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
) : ProducingDataRepository(coroutineDispatcher, coroutineScope) {
    abstract suspend fun getMyProfile(): LiveData<GetMyProfileDataResult>
    abstract suspend fun updateMyProfile(updateProfileData: DataMyProfileUpdateData)
    abstract suspend fun deleteMyProfile()
}