package com.qubacy.geoqq.ui._di.module

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@Module(subcomponents = [])
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