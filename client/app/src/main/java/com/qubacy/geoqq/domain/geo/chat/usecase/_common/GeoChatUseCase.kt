package com.qubacy.geoqq.domain.geo.chat.usecase._common

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.result.added.GeoMessageAddedDataResult
import com.qubacy.geoqq.domain._common.usecase._common.error.middleware.ErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.AuthorizedUseCase
import com.qubacy.geoqq.domain._common.usecase.aspect.authorized.error.middleware.AuthorizedErrorMiddleware
import com.qubacy.geoqq.domain._common.usecase.aspect.user.UserAspectUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.UpdatableUseCase
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler._common.UpdateErrorHandler
import com.qubacy.geoqq.domain._common.usecase.base.updatable.update.error.handler.common.UpdateCommonErrorHandler
import com.qubacy.geoqq.domain.geo._common.model.toGeoMessage
import com.qubacy.geoqq.domain.geo.chat.usecase._common.result.message.added.GeoMessageAddedDomainResult

abstract class GeoChatUseCase(
    errorSource: LocalErrorDatabaseDataSource
) : UpdatableUseCase(errorSource = errorSource), AuthorizedUseCase, UserAspectUseCase {
    override fun generateErrorMiddlewares(): Array<ErrorMiddleware> {
        return arrayOf(AuthorizedErrorMiddleware(this))
    }

    override fun generateUpdateErrorHandlers(): Array<UpdateErrorHandler> {
        return arrayOf(UpdateCommonErrorHandler(this))
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
    open fun processGeoMessageAddedDataResult(
        dataResult: GeoMessageAddedDataResult
    ): GeoMessageAddedDomainResult {
        val message = dataResult.message.toGeoMessage()

        return GeoMessageAddedDomainResult(message = message)
    }
}