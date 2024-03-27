package com.qubacy.geoqq.data.mate.chat.repository.source.local.module

import android.content.Context
import com.qubacy.geoqq.data.mate.chat.repository.source.local.LocalMateChatDataSource
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LocalMateChatDataSourceModule {
    @Provides
    fun provideLocalMateChatDataSource(
        @ApplicationContext context: Context
    ): LocalMateChatDataSource {
        val db = (context as CustomApplication).db

        return db.mateChatDao()
    }
}