package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.geo.chat.usecase._common.GeoChatUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.impl.GeoChatViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.impl.GeoChatViewModelFactoryQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object GeoChatViewModelModule {
    @Provides
    @GeoChatViewModelFactoryQualifier
    fun provideGeoChatViewModelFactory(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        geoChatUseCase: GeoChatUseCase
    ): ViewModelProvider.Factory {
        return GeoChatViewModelFactory(localErrorDataSource, geoChatUseCase)
    }
}