package com.qubacy.geoqq.domain.myprofile.usecase

import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.myprofile.model.profile.toMyProfile
import com.qubacy.geoqq.domain.myprofile.model.update.MyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.model.update.toDataMyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.usecase.result.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.update.UpdateMyProfileDomainResult

class MyProfileUseCase(
    errorDataRepository: ErrorDataRepository,
    private val mMyProfileDataRepository: MyProfileDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository) {
    fun getMyProfile() {
        executeLogic({
            val getMyProfileResult = mMyProfileDataRepository.getMyProfile().await()
            val myProfile = getMyProfileResult.myProfile.toMyProfile()

            mResultFlow.emit(GetMyProfileDomainResult(myProfile = myProfile))
        }) { GetMyProfileDomainResult(error = it) }
    }

    fun updateMyProfile(myProfileUpdateData: MyProfileUpdateData) {
        executeLogic({
            mMyProfileDataRepository.updateMyProfile(myProfileUpdateData.toDataMyProfileUpdateData())
        }) { UpdateMyProfileDomainResult(error = it) }
    }
}