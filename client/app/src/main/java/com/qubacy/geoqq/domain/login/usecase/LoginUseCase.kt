package com.qubacy.geoqq.domain.login.usecase

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    mErrorDataRepository: ErrorDataRepository,

) : UseCase(mErrorDataRepository) {

}