package com.qubacy.geoqq.data.geo.message.repository.impl

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.qubacy.geoqq._common.util.livedata.extension.await
import com.qubacy.geoqq._common.util.livedata.extension.awaitUntilVersion
import com.qubacy.geoqq.data._common.model.message.DataMessage
import com.qubacy.geoqq.data._common.model.message.toDataMessage
import com.qubacy.geoqq.data._common.repository._common.result.DataResult
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.error.WebSocketErrorResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result.payload.WebSocketPayloadResult
import com.qubacy.geoqq.data._common.repository.message.util.extension.resolveGetMessagesResponse
import com.qubacy.geoqq.data._common.repository.producing.source.ProducingDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.GeoMessageDataRepository
import com.qubacy.geoqq.data.geo.message.repository._common.result.added.GeoMessageAddedDataResult
import com.qubacy.geoqq.data.geo.message.repository._common.result.get.GetGeoMessagesDataResult
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.rest._common.RemoteGeoMessageHttpRestDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.RemoteGeoMessageHttpWebSocketDataSource
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.payload.added.GeoMessageAddedEventPayload
import com.qubacy.geoqq.data.geo.message.repository._common.source.remote.http.websocket._common.event.type.GeoMessageEventType
import com.qubacy.geoqq.data.user.repository._common.UserDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

class GeoMessageDataRepositoryImpl(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    private val mErrorSource: LocalErrorDatabaseDataSource,
    private val mUserDataRepository: UserDataRepository,
    private val mRemoteGeoMessageHttpRestDataSource: RemoteGeoMessageHttpRestDataSource,
    private val mRemoteGeoMessageHttpWebSocketDataSource: RemoteGeoMessageHttpWebSocketDataSource
) : GeoMessageDataRepository(coroutineDispatcher, coroutineScope) {
    companion object {
        const val TAG = "GeoMsgDataRepositoryImpl"
    }

    override fun getProducingDataSources(): Array<ProducingDataSource> {
        return arrayOf(mRemoteGeoMessageHttpWebSocketDataSource)
    }

    override fun generateGeneralResultFlow(): Flow<DataResult> {
        return merge(
            mResultFlow,
            mRemoteGeoMessageHttpWebSocketDataSource.eventFlow
                .mapNotNull { mapWebSocketResultToDataResult(it) }
        )
    }

    override suspend fun getMessages(
        radius: Int,
        longitude: Float,
        latitude: Float
    ): LiveData<GetGeoMessagesDataResult> {
        val resultLiveData = MutableLiveData<GetGeoMessagesDataResult>()

        val getMessagesResponse = mRemoteGeoMessageHttpRestDataSource.getMessages(radius, longitude, latitude)
        val resolveGetMessagesResultLiveData = resolveGetMessagesResponse(
            mUserDataRepository, getMessagesResponse)

        CoroutineScope(coroutineContext).launch {
            var version = 0

            while (true) {
                val resolveGetMessagesResult = resolveGetMessagesResultLiveData
                    .awaitUntilVersion(version)
                val messages = resolveGetMessagesResult.messages

                ++version

                resultLiveData.postValue(
                    GetGeoMessagesDataResult(
                    resolveGetMessagesResult.isNewest, messages)
                )

                if (resolveGetMessagesResult.isNewest) return@launch startProducingUpdates()
            }
        }

        return resultLiveData
    }

    override suspend fun sendMessage(
        text: String,
        longitude: Float,
        latitude: Float
    ) {
        mRemoteGeoMessageHttpWebSocketDataSource.sendMessage(text, latitude, longitude)

        // todo: delete (for debug only!):
        //mRemoteGeoMessageHttpRestDataSource.sendMessage(text, radius, longitude, latitude)
    }

    override suspend fun sendLocation(longitude: Float, latitude: Float, radius: Int) {
        mRemoteGeoMessageHttpWebSocketDataSource.sendLocation(latitude, longitude, radius)
    }

    override fun processWebSocketErrorResult(webSocketErrorResult: WebSocketErrorResult): DataResult? {
        // todo: what to do here?

        return super.processWebSocketErrorResult(webSocketErrorResult)
    }

    override fun processWebSocketPayloadResult(
        webSocketPayloadResult: WebSocketPayloadResult
    ): DataResult {
        return when (webSocketPayloadResult.type) {
            GeoMessageEventType.GEO_MESSAGE_ADDED_EVENT_TYPE.title ->
                processGeoMessageAddedEventPayload(
                    webSocketPayloadResult.payload as GeoMessageAddedEventPayload)
            else -> throw IllegalArgumentException()
        }
    }

    private fun processGeoMessageAddedEventPayload(
        payload: GeoMessageAddedEventPayload
    ): DataResult {
        lateinit var dataMessage: DataMessage

        Log.d(TAG, "processGeoMessageAddedEventPayload(): entering;")

        runBlocking {
            val getUserResult = mUserDataRepository.getUsersByIds(listOf(payload.userId)).await() // todo: alright?

            dataMessage = payload.toDataMessage(getUserResult.users.first())

            Log.d(TAG, "processGeoMessageAddedEventPayload(): dataMessage = $dataMessage;")
        }

        return GeoMessageAddedDataResult(dataMessage)
    }
}