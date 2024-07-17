package com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket.impl

import android.util.Log
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.packet.event.json.adapter.EventJsonAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.action.PackagedAction
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.RemoteMateMessageHttpWebSocketDataSource
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.action.payload.add.AddMateMessageActionPayload
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.action.type.MateMessageActionType
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.payload.added.MateMessageAddedEventPayload
import com.qubacy.geoqq.data.mate.message.repository._common.source.remote.http.websocket._common.event.type.MateMessageEventType
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

class RemoteMateMessageHttpWebSocketDataSourceImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    coroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher),
    override val mEventJsonAdapter: EventJsonAdapter,
    override val mErrorDataSource: LocalErrorDatabaseDataSource,
    webSocketAdapter: WebSocketAdapter,
    private val mMateMessageEventPayloadJsonAdapter: JsonAdapter<MateMessageAddedEventPayload>,
    private val mAddMateMessageActionPayloadJsonAdapter: JsonAdapter<AddMateMessageActionPayload>
) : RemoteMateMessageHttpWebSocketDataSource(coroutineDispatcher, coroutineScope) {
    companion object {
        const val TAG = "RemoteMateMsgHttpWebSctDataSrcImpl"
    }

    init {
        Log.d(TAG, "init(): webSocketAdapter = $webSocketAdapter;")

        mEventJsonAdapter.setCallback(this)

        mWebSocketAdapter = webSocketAdapter
    }

    override fun getEventPayloadJsonAdapterByType(type: String): JsonAdapter<*>? {
        Log.d(TAG, "getEventPayloadJsonAdapterByType(): type = $type;")

        return when (type) {
            MateMessageEventType.MATE_MESSAGE_ADDED_EVENT_TYPE.title ->
                mMateMessageEventPayloadJsonAdapter
            else -> null
        }
    }

    override fun sendMessage(chatId: Long, text: String) {
        val payload = AddMateMessageActionPayload(chatId, text)
        val payloadString = mAddMateMessageActionPayloadJsonAdapter.toJson(payload)
        val action = PackagedAction(MateMessageActionType.ADD_MATE_MESSAGE.title, payloadString)

        mWebSocketAdapter.sendAction(action)
    }
}