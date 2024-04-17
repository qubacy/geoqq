package com.qubacy.geoqq.domain.mate.requests.usecase

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.mate.request.repository.MateRequestDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import javax.inject.Inject

class MateRequestsUseCase @Inject constructor(
    errorDataRepository: ErrorDataRepository,
    private val mMateRequestDataRepository: MateRequestDataRepository,
    private val mUserDataRepository: UserDataRepository
) : UseCase(mErrorDataRepository = errorDataRepository) {

}