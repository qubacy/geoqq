package com.qubacy.geoqq._common.util.livedata.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

suspend fun <T>LiveData<T>.await(): T {
    return withContext(Dispatchers.Main.immediate) {
        suspendCancellableCoroutine { continuation ->
            val observer = object : Observer<T> {
                override fun onChanged(value: T) {
                    removeObserver(this)
                    continuation.resume(value)
                }
            }

            observeForever(observer)

            continuation.invokeOnCancellation {
                launch(Dispatchers.Main.immediate) {
                    removeObserver(observer)
                }
            }
        }
    }
}