package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.MyProfileFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.MyProfileDeletedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.profile.get.GetMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.MyProfileUpdatedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.profile.update.UpdateMyProfileUiOperation

class MyProfileUiOperationHandler(
    fragment: MyProfileFragment
) : UiOperationHandler<MyProfileFragment>(fragment) {
    override fun handleUiOperation(uiOperation: UiOperation): Boolean {
        when (uiOperation::class) {
            GetMyProfileUiOperation::class -> {
                uiOperation as GetMyProfileUiOperation

                fragment.onMyProfileFragmentGetMyProfile(uiOperation.myProfile)
            }
            UpdateMyProfileUiOperation::class -> {
                uiOperation as UpdateMyProfileUiOperation

                fragment.onMyProfileFragmentUpdateMyProfile(uiOperation.myProfile)
            }
            MyProfileUpdatedUiOperation::class -> {
                fragment.onMyProfileFragmentMyProfileUpdated()
            }
            MyProfileDeletedUiOperation::class -> {
                fragment.onMyProfileFragmentDeleteMyProfile()
            }
            else -> return false
        }

        return true
    }
}