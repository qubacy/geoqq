package com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.operation.handler

import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.model.operation._common.UiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.stateful.operation.handler._common.UiOperationHandler
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.MyProfileFragment
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.DeleteMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.GetMyProfileUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.myprofile.model.operation.UpdateMyProfileUiOperation

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
                fragment.onMyProfileFragmentUpdateMyProfile()
            }
            DeleteMyProfileUiOperation::class -> {
                fragment.onMyProfileFragmentDeleteMyProfile()
            }
            else -> return false
        }

        return true
    }
}