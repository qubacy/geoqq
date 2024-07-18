package com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common

import android.util.Log
import com.qubacy.geoqq._common.struct.flow.MutableColdFlow
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.result._common.WebSocketResult
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.WebSocketAdapter
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.listener.WebSocketEventListener
import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket._common.socket.adapter._common.event.model._common.WebSocketEvent
import com.qubacy.geoqq.data._common.repository.producing.source.ProducingDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

abstract class RemoteHttpWebSocketDataSource @OptIn(ExperimentalCoroutinesApi::class) constructor(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default.limitedParallelism(1),
    protected val mCoroutineScope: CoroutineScope = CoroutineScope(coroutineDispatcher)
) : ProducingDataSource, WebSocketEventListener {
    companion object {
        const val TAG = "RemoteHttpWebSocketDataSrc"
    }

    protected lateinit var mWebSocketAdapter: WebSocketAdapter

    protected var mEventFlow: MutableColdFlow<WebSocketResult> = MutableColdFlow()
    val eventFlow: Flow<WebSocketResult> get() = mEventFlow.flow

    @Volatile
    private var mIsStarted: Boolean = false

    @Synchronized
    override fun startProducing() {
        Log.d(TAG, "startProducing(): mIsStarted = $mIsStarted; class = ${this.javaClass.simpleName}")

        if (mIsStarted) return

        mIsStarted = true

        Log.d(TAG, "startProducing(): entering.. class = ${this.javaClass.simpleName}")

        mWebSocketAdapter.addEventListener(this)
    }

    @Synchronized
    override fun stopProducing() {
        if (!mIsStarted) return

        mIsStarted = false

        Log.d(TAG, "stopProducing(): entering.. class = ${this.javaClass.simpleName}")

        mWebSocketAdapter.removeEventListener(this)
    }

    override fun onEventGotten(event: WebSocketEvent) {
        mCoroutineScope.launch {
            val result = processEvent(event) ?: return@launch

            Log.d(TAG, "onEventGotten(): result = $result;")

            mEventFlow.emit(result)
        }
    }

    protected abstract fun processEvent(event: WebSocketEvent): WebSocketResult?

    override fun reset() {
        Log.d(TAG, "reset(): class = ${javaClass.simpleName};")

        mEventFlow = MutableColdFlow()
    }
}