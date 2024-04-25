package com.qubacy.geoqq.data._common.repository._common.source.local.database.module

import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.local.database.Database
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): Database {
        val application = (context as CustomApplication)

        return application.db
    }
}