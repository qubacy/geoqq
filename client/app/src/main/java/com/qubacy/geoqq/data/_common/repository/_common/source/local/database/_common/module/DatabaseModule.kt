package com.qubacy.geoqq.data._common.repository._common.source.local.database._common.module

import android.content.Context
import com.qubacy.geoqq.data._common.repository._common.source.local.database._common.Database
import com.qubacy.geoqq.ui.application.CustomApplication
import dagger.Module
import dagger.Provides

@Module
abstract class DatabaseModule {
    @Provides
    fun provideDatabase(
        context: Context
    ): Database {
        val application = (context as CustomApplication)

        return application.db
    }
}