package com.qubacy.geoqq.domain._common.usecase.aspect.user

import com.qubacy.geoqq.data.user.repository._common.result.updated.UserUpdatedDataResult
import com.qubacy.geoqq.domain._common.model.user.toUser
import com.qubacy.geoqq.domain._common.usecase._common.result._common.DomainResult
import com.qubacy.geoqq.domain._common.usecase.aspect.user.result.update.UserUpdatedDomainResult

interface UserAspectUseCase {
    fun processUserUpdatedDataResult(dataResult: UserUpdatedDataResult): DomainResult? {
        val user = dataResult.user.toUser()

        return UserUpdatedDomainResult(user = user)
    }
}