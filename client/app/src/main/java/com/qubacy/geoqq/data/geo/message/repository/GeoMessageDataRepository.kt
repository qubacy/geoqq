package com.qubacy.geoqq.data.geo.message.repository

import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.LocalErrorDataSource
import com.qubacy.geoqq.data._common.repository.message.MessageDataRepository
import com.qubacy.geoqq.data._common.repository.message.util.extension.resolveGetMessagesResponse
import com.qubacy.geoqq.data._common.repository.producing.ProducingDataRepository
import com.qubacy.geoqq.data.geo.message.repository.result.GetGeoMessagesDataResult
import com.qubacy.geoqq.data.geo.message.repository.source.http.HttpGeoChatDataSource
import com.qubacy.geoqq.data.user.repository.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class GeoMessageDataRepository @Inject constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDataSource,
    private val mUserDataRepository: UserDataRepository,
    private val mHttpGeoChatDataSource: HttpGeoChatDataSource,
    // todo: add a ws source..
) : ProducingDataRepository(coroutineDispatcher, coroutineScope), MessageDataRepository {
    suspend fun getMessages(
        radius: Int,
        longitude: Float,
        latitude: Float
    ): GetGeoMessagesDataResult {
        val getMessagesResponse = mHttpGeoChatDataSource.getMessages(radius, longitude, latitude)

        val dataMessages = resolveGetMessagesResponse(mUserDataRepository, getMessagesResponse)

        return GetGeoMessagesDataResult(dataMessages)
    }

    suspend fun sendMessage(
        text: String,
        radius: Int,
        longitude: Float,
        latitude: Float
    ) {
        // todo: implement using the websocket data source;


        // todo: delete (for debug only!):
        mHttpGeoChatDataSource.sendMessage(text, radius, longitude, latitude)
    }
}