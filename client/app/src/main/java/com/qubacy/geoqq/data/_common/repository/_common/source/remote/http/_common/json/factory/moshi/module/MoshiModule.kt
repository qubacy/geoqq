package com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.json.factory.moshi.module

import com.qubacy.geoqq.data._common.repository._common.source.remote.http._common.response._common.json.adapter.StringJsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class MoshiModule {
    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(String::class.java, StringJsonAdapter())
            .build()
    }
}