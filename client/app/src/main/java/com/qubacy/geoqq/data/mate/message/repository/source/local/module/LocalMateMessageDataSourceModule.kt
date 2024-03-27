package com.qubacy.geoqq.data.mate.message.repository.source.local.module

import android.content.Context
import com.qubacy.geoqq.data.mate.message.repository.source.local.LocalMateMessageDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalMateMessageDataSourceModule {
    @Provides
    fun provideLocalMateMessageDataSource(
        @ApplicationContext context: Context
    ): LocalMateMessageDataSource {
        val db = (context as CustomApplication).db

        return db.mateMessageDao()
    }
}