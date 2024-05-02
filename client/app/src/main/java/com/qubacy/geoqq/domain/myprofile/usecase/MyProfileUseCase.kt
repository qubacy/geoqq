package com.qubacy.geoqq.domain.myprofile.usecase

import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.myprofile.repository.MyProfileDataRepository
import com.qubacy.geoqq.data.myprofile.repository.result.GetMyProfileDataResult
import com.qubacy.geoqq.data.auth.repository.AuthDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.myprofile.model.profile.toMyProfile
import com.qubacy.geoqq.domain.myprofile.model.update.MyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.model.update.toDataMyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.usecase.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.logout.usecase.result.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.update.UpdateMyProfileDomainResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyProfileUseCase @Inject constructor(
    errorDataRepository: ErrorDataRepository,
    private val mLogoutUseCase: LogoutUseCase,
    private val mMyProfileDataRepository: MyProfileDataRepository,
    private val mTokenDataRepository: AuthDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository), AuthorizedUseCase {
    fun getMyProfile() {
        executeLogic({
            val getMyProfileResult = mMyProfileDataRepository.getMyProfile().await()
            val myProfile = getMyProfileResult.myProfile.toMyProfile()

            mResultFlow.emit(GetMyProfileDomainResult(myProfile = myProfile))
        }, { GetMyProfileDomainResult(error = it) }, ::authorizedErrorMiddleware)
    }

    fun updateMyProfile(myProfileUpdateData: MyProfileUpdateData) {
        executeLogic({
            mMyProfileDataRepository.updateMyProfile(myProfileUpdateData.toDataMyProfileUpdateData())

            if (myProfileUpdateData.security != null) logout()

            mResultFlow.emit(UpdateMyProfileDomainResult())
        }, { UpdateMyProfileDomainResult(error = it) }, ::authorizedErrorMiddleware)
    }

    fun deleteMyProfile() {
        executeLogic({
            mMyProfileDataRepository.deleteMyProfile()
            mTokenDataRepository.logout()

            mResultFlow.emit(DeleteMyProfileDomainResult())
        }, { DeleteMyProfileDomainResult(error = it) }, ::authorizedErrorMiddleware)
    }

    fun logout() {
        executeLogic({
            mTokenDataRepository.logout()

            mResultFlow.emit(LogoutDomainResult())
        }, { LogoutDomainResult(error = it) }, ::authorizedErrorMiddleware)
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

    override fun getLogoutUseCase(): LogoutUseCase {
        return mLogoutUseCase
    }
}