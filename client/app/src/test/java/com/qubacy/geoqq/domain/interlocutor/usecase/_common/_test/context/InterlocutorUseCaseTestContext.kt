package com.qubacy.geoqq.domain.interlocutor.usecase._common._test.context

import com.qubacy.geoqq.data.user.repository._common._test.context.UserDataRepositoryTestContext
import com.qubacy.geoqq.domain._common.model.user.toUser

object InterlocutorUseCaseTestContext {
    val DEFAULT_USER = UserDataRepositoryTestContext.DEFAULT_DATA_USER.toUser()
}