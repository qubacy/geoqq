package com.qubacy.geoqq.domain.myprofile.usecase.impl

import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.auth.repository._common.AuthDataRepository
import com.qubacy.geoqq.data.myprofile.repository._common.MyProfileDataRepository
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler
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
import com.qubacy.geoqq.domain.myprofile.usecase._common.update.handler.MyProfileDataUpdateHandler
import javax.inject.Inject

class MyProfileUseCaseImpl @Inject constructor(
    errorSource: LocalErrorDatabaseDataSource,
    private val mLogoutUseCase: LogoutUseCase,
    private val mMyProfileDataRepository: MyProfileDataRepository,
    private val mAuthDataRepository: AuthDataRepository
) : MyProfileUseCase(errorSource) {
    override fun getUpdatableRepositories(): Array<ProducingDataRepository> {
        return arrayOf(mMyProfileDataRepository, mAuthDataRepository)
    }

    override fun generateDataUpdateHandlers(): Array<DataUpdateHandler<*>> {
        return super.generateDataUpdateHandlers()
            .plus(MyProfileDataUpdateHandler(this))
    }

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

        }, { GetMyProfileDomainResult(error = it) })
    }

    override fun updateMyProfile(myProfileUpdateData: MyProfileUpdateData) {
        executeLogic({
            mMyProfileDataRepository.updateMyProfile(myProfileUpdateData.toDataMyProfileUpdateData())

            if (myProfileUpdateData.security != null) logout()

            mResultFlow.emit(MyProfileUpdatedDomainResult())
        }, { MyProfileUpdatedDomainResult(error = it) })
    }

    override fun deleteMyProfile() {
        executeLogic({
            mMyProfileDataRepository.deleteMyProfile()
            mAuthDataRepository.logout()

            mResultFlow.emit(DeleteMyProfileDomainResult())
        }, { DeleteMyProfileDomainResult(error = it) })
    }

    override fun logout() {
        executeLogic({
            mAuthDataRepository.logout()

            mResultFlow.emit(LogoutDomainResult())
        }, { LogoutDomainResult(error = it) })
    }

    override fun passCoroutineScopeToDependencies() {
        mLogoutUseCase.setCoroutineScope(mCoroutineScope)
        mAuthDataRepository.setCoroutineScope(mCoroutineScope)
        mMyProfileDataRepository.setCoroutineScope(mCoroutineScope)
    }

    override fun getLogoutUseCase(): LogoutUseCase {
        return mLogoutUseCase
    }
}