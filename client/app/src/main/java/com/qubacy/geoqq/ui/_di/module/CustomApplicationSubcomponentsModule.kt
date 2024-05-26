package com.qubacy.geoqq.ui._di.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http.websocket.socket._di.component.WebSocketComponent
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Module(subcomponents = [
    WebSocketComponent::class
])
abstract class CustomApplicationSubcomponentsModule {
    companion object {
        // todo: delete (for experiments only):

        @JvmStatic
        @Provides
        fun provideCoroutineDispatcher(): CoroutineDispatcher {
            return Dispatchers.Default
        }

        @JvmStatic
        @Provides
        fun provideCoroutineScope(
            coroutineDispatcher: CoroutineDispatcher
        ): CoroutineScope {
            return CoroutineScope(coroutineDispatcher)
        }
    }
}