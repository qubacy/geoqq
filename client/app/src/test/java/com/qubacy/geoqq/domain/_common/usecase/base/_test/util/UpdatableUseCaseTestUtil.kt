package com.qubacy.geoqq.domain._common.usecase.base._test.util

import com.qubacy.geoqq.domain._common.usecase.base._common.UseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.cancellation.CancellationException

fun runCoroutineTestCase(useCase: UseCase, action: suspend () -> Unit) = runTest {
    val dispatcher = StandardTestDispatcher() +
            CoroutineExceptionHandler { _, exception ->
                if (exception !is CancellationException) throw exception
            }
    val scope = CoroutineScope(dispatcher)

    useCase.setCoroutineScope(scope)

    action.invoke()
    scope.cancel()
}