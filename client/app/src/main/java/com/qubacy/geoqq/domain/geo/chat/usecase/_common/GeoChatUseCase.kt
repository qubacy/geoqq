package com.qubacy.geoqq.domain.geo.chat.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain._common.usecase._common.error.middleware.ErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware.AuthorizedErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.user.UserAspectUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.UpdatableUseCase

abstract class GeoChatUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UpdatableUseCase(errorSource = errorSource), AuthorizedUseCase, UserAspectUseCase {
    override fun generateErrorMiddlewares(): Array<ErrorMiddleware> {
        return arrayOf(AuthorizedErrorMiddleware(this))
    }

    abstract fun getMessages(
        radius: Int,
        longitude: Float,
        latitude: Float
    )
    abstract fun sendMessage(
        text: String,
        radius: Int,
        latitude: Float,
        longitude: Float
    )
    abstract fun getLocalUserId(): Long
    abstract fun getInterlocutor(interlocutorId: Long)
    abstract fun sendMateRequestToInterlocutor(interlocutorId: Long)
}