package com.qubacy.geoqq.domain.myprofile.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain.myprofile.model.update.MyProfileUpdateData

abstract class MyProfileUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    abstract fun getMyProfile()
    abstract fun updateMyProfile(myProfileUpdateData: MyProfileUpdateData)
    abstract fun deleteMyProfile()
    abstract fun logout()
}