package com.qubacy.geoqq._common.struct.flow

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MutableColdFlow<T> {
    private val mChannel = Channel<T>(Channel.CONFLATED)
    val flow: Flow<T> = flow {
        mChannel.consumeEach {
            emit(it)
        }
    }

    suspend fun emit(value: T) {
        mChannel.send(value)
    }
}