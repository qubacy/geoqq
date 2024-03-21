package com.qubacy.geoqq.domain.mate.chats.usecase

import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.domain._common.usecase._common.UseCase

class MateChatsUseCase(
    mErrorDataRepository: ErrorDataRepository,

) : UseCase(mErrorDataRepository) {

}