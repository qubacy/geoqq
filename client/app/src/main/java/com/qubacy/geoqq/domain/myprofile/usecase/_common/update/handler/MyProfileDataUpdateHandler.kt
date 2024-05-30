package com.qubacy.geoqq.domain.myprofile.usecase._common.update.handler

import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data.myprofile.repository._common.result.GetMyProfileDataResult
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.handler.DataUpdateHandler
import com.qubacy.geoqq.domain.myprofile.model.profile.toMyProfile
import com.qubacy.geoqq.domain.myprofile.usecase._common.MyProfileUseCase
import com.qubacy.geoqq.domain.myprofile.usecase._common.result.profile.get.GetMyProfileDomainResult

class MyProfileDataUpdateHandler(
    myProfileUseCase: MyProfileUseCase
) : DataUpdateHandler<MyProfileUseCase>(myProfileUseCase) {
    override fun handle(dataUpdate: DataResult): DomainResult? {
        if (dataUpdate !is GetMyProfileDataResult) return null

        return processGetMyProfileDataResult(dataUpdate)
    }

    private fun processGetMyProfileDataResult(
        getMyProfileResult: GetMyProfileDataResult
    ): GetMyProfileDomainResult {
        val myProfile = getMyProfileResult.myProfile.toMyProfile()

        return GetMyProfileDomainResult(myProfile = myProfile)
    }
}