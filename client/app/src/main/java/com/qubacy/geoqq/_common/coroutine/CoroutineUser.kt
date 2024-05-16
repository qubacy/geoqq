package com.qubacy.geoqq._common.coroutine

import androidx.annotation.CallSuper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

open class CoroutineUser(
    protected var mCoroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    protected var mCoroutineScope: CoroutineScope = CoroutineScope(mCoroutineDispatcher)
) {
    fun setCoroutineScope(coroutineScope: CoroutineScope) {
        mCoroutineScope = coroutineScope

        onCoroutineScopeSet()
    }

    @CallSuper
    protected open fun onCoroutineScopeSet() {}

    fun setCoroutineDispatcher(coroutineDispatcher: CoroutineDispatcher) {
        mCoroutineDispatcher = coroutineDispatcher
    }
}