package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error._common.LocalErrorDatabaseDataSource
import com.qubacy.geoqq.domain.mate.chat.usecase._common.MateChatUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.impl.MateChatViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.impl.MateChatViewModelFactoryQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object MateChatViewModelModule {
    @Provides
    @MateChatViewModelFactoryQualifier
    fun provideMateChatViewModel(
        localErrorDataSource: LocalErrorDatabaseDataSource,
        mateChatsUseCase: MateChatUseCase
    ): ViewModelProvider.Factory {
        return MateChatViewModelFactory(localErrorDataSource, mateChatsUseCase)
    }
}