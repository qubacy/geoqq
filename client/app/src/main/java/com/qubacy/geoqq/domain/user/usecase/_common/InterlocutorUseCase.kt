package com.qubacy.geoqq.domain.user.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase.base._common.UseCase
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.AuthorizedUseCase

abstract class InterlocutorUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UseCase(mErrorSource = errorSource), AuthorizedUseCase {
    abstract fun getInterlocutor(interlocutorId: Long)
}