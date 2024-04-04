package com.qubacy.geoqq.domain.myprofile.usecase

import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileDataResult
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain.myprofile.model.profile.toMyProfile
import com.qubacy.geoqq.domain.myprofile.model.update.MyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.model.update.toDataMyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.usecase.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.logout.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.update.UpdateMyProfileDomainResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyProfileUseCase @Inject constructor(
    errorDataRepository: ErrorDataRepository,
    private val mMyProfileDataRepository: MyProfileDataRepository,
    private val mTokenDataRepository: TokenDataRepository
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

            if (myProfileUpdateData.security != null) logout()

            mResultFlow.emit(UpdateMyProfileDomainResult())
        }) { UpdateMyProfileDomainResult(error = it) }
    }

    fun deleteMyProfile() {
        executeLogic({
            mMyProfileDataRepository.deleteMyProfile()
            mTokenDataRepository.clearTokens()

            mResultFlow.emit(DeleteMyProfileDomainResult())
        }) { DeleteMyProfileDomainResult(error = it) }
    }

    fun logout() {
        executeLogic({
            mTokenDataRepository.clearTokens()

            mResultFlow.emit(LogoutDomainResult())
        }) { LogoutDomainResult(error = it) }
    }

    override fun onCoroutineScopeSet() {
        super.onCoroutineScopeSet()

        mCoroutineScope.launch {
            mMyProfileDataRepository.resultFlow.collect {
                processCollectedDataResult(it)
            }
        }
    }

    private suspend fun processCollectedDataResult(dataResult: DataResult) {
        when (dataResult::class) {
            GetMyProfileDataResult::class ->
                processGetMyProfileDataResult(dataResult as GetMyProfileDataResult)
            else -> throw IllegalArgumentException()
        }
    }

    private suspend fun processGetMyProfileDataResult(
        getMyProfileResult: GetMyProfileDataResult
    ) {
        val myProfile = getMyProfileResult.myProfile.toMyProfile()

        mResultFlow.emit(GetMyProfileDomainResult(myProfile = myProfile))
    }
}