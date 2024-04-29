package com.qubacy.geoqq.data.geo.message.repository

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.executor.HttpCallExecutor
import com.qubacy.geoqq.data._common.repository.message.MessageDataRepository
import com.qubacy.geoqq.data._common.repository.message.util.extension.resolveGetMessagesResponse
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
import com.qubacy.geoqq.data.geo.message.repository.result.GetGeoMessagesDataResult
import com.qubacy.geoqq.data.geo.message.repository.source.http.HttpGeoChatDataSource
import com.qubacy.geoqq.data.token.repository.TokenDataRepository
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class GeoMessageDataRepository @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorDataRepository: ErrorDataRepository,
    private val mTokenDataRepository: TokenDataRepository,
    private val mUserDataRepository: UserDataRepository,
    private val mHttpGeoChatDataSource: HttpGeoChatDataSource,
    private val mHttpCallExecutor: HttpCallExecutor,
    // todo: add a ws source..
) : ProducingDataRepository(coroutineDispatcher, coroutineScope), MessageDataRepository {
    suspend fun getMessages(
        radius: Int,
        longitude: Double,
        latitude: Double
    ): GetGeoMessagesDataResult {
        val accessToken = mTokenDataRepository.getTokens().accessToken

        val getMessagesCall = mHttpGeoChatDataSource
            .getMessages(accessToken, radius, longitude, latitude)
        val getMessagesResponse = mHttpCallExecutor.executeNetworkRequest(getMessagesCall)

        val dataMessages = resolveGetMessagesResponse(mUserDataRepository, getMessagesResponse)

        return GetGeoMessagesDataResult(dataMessages)
    }

    suspend fun sendMessage(
        text: String,
        longitude: Double,
        latitude: Double // todo: is it enough?
    ) {
        // todo: implement using the websocket data source;

    }
}