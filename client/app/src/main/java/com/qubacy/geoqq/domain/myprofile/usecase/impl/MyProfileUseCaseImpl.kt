package com.qubacy.geoqq.domain.myprofile.usecase.impl

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.myprofile.repository._common.result.GetMyProfileDataResult
import com.qubacy.geoqq.data.myprofile.repository._common.MyProfileDataRepository
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain.logout.usecase._common.LogoutUseCase
import com.qubacy.geoqq.domain.myprofile.model.profile.toMyProfile
import com.qubacy.geoqq.domain.myprofile.model.update.MyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.model.update.toDataMyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.profile.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.logout.usecase._common.result.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase._common.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.profile.update.UpdateMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.update.MyProfileUpdatedDomainResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyProfileUseCaseImpl @Inject constructor(
    errorSource: LocalErrorDatabaseDataSource,
    private val mLogoutUseCase: LogoutUseCase,
    private val mMyProfileDataRepository: MyProfileDataRepository,
    private val mAuthDataRepository: AuthDataRepository
) : MyProfileUseCase(errorSource) {
    override fun getMyProfile() {
        executeLogic({
            var version = 0

            val getMyProfileResultLiveData = mMyProfileDataRepository.getMyProfile()

            val initGetMyProfileResult = getMyProfileResultLiveData.awaitUntilVersion(version)
            val initMyProfile = initGetMyProfileResult.myProfile.toMyProfile()

            mResultFlow.emit(GetMyProfileDomainResult(myProfile = initMyProfile))

            if (initGetMyProfileResult.isNewest) return@executeLogic

            ++version

            val newestGetMyProfileResult = getMyProfileResultLiveData.awaitUntilVersion(version)
            val newestMyProfile = newestGetMyProfileResult.myProfile.toMyProfile()

            mResultFlow.emit(UpdateMyProfileDomainResult(myProfile = newestMyProfile))

        }, { GetMyProfileDomainResult(error = it) }, ::authorizedErrorMiddleware)
    }

    override fun updateMyProfile(myProfileUpdateData: MyProfileUpdateData) {
        executeLogic({
            mMyProfileDataRepository.updateMyProfile(myProfileUpdateData.toDataMyProfileUpdateData())

            if (myProfileUpdateData.security != null) logout()

            mResultFlow.emit(MyProfileUpdatedDomainResult())
        }, { MyProfileUpdatedDomainResult(error = it) }, ::authorizedErrorMiddleware)
    }

    override fun deleteMyProfile() {
        executeLogic({
            mMyProfileDataRepository.deleteMyProfile()
            mAuthDataRepository.logout()

            mResultFlow.emit(DeleteMyProfileDomainResult())
        }, { DeleteMyProfileDomainResult(error = it) }, ::authorizedErrorMiddleware)
    }

    override fun logout() {
        executeLogic({
            mAuthDataRepository.logout()

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