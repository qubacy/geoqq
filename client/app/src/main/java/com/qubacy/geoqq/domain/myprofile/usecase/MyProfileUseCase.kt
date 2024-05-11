package com.qubacy.geoqq.domain.myprofile.usecase

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.data.myprofile.repository.impl.MyProfileDataRepositoryImpl
import com.qubacy.geoqq.data.myprofile.repository._common.result.GetMyProfileDataResult
import com.qubacy.geoqq.data.auth.repository.impl.AuthDataRepositoryImpl
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.error.middleware.authorizedErrorMiddleware
import com.qubacy.geoqq.domain.logout.usecase.LogoutUseCase
import com.qubacy.geoqq.domain.myprofile.model.profile.toMyProfile
import com.qubacy.geoqq.domain.myprofile.model.update.MyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.model.update.toDataMyProfileUpdateData
import com.qubacy.geoqq.domain.myprofile.usecase.result.delete.DeleteMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.profile.get.GetMyProfileDomainResult
import com.qubacy.geoqq.domain.logout.usecase.result.LogoutDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.profile.update.UpdateMyProfileDomainResult
import com.qubacy.geoqq.domain.myprofile.usecase.result.update.MyProfileUpdatedDomainResult
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyProfileUseCase @Inject constructor(
    errorSource: LocalErrorDatabaseDataSourceImpl,
    private val mLogoutUseCase: LogoutUseCase,
    private val mMyProfileDataRepository: MyProfileDataRepositoryImpl,
    private val mAuthDataRepository: AuthDataRepositoryImpl
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    fun getMyProfile() {
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

    fun updateMyProfile(myProfileUpdateData: MyProfileUpdateData) {
        executeLogic({
            mMyProfileDataRepository.updateMyProfile(myProfileUpdateData.toDataMyProfileUpdateData())

            if (myProfileUpdateData.security != null) logout()

            mResultFlow.emit(MyProfileUpdatedDomainResult())
        }, { MyProfileUpdatedDomainResult(error = it) }, ::authorizedErrorMiddleware)
    }

    fun deleteMyProfile() {
        executeLogic({
            mMyProfileDataRepository.deleteMyProfile()
            mAuthDataRepository.logout()

            mResultFlow.emit(DeleteMyProfileDomainResult())
        }, { DeleteMyProfileDomainResult(error = it) }, ::authorizedErrorMiddleware)
    }

    fun logout() {
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