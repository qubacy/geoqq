package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.domain.mate.chat.usecase.MateChatUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.MateChatViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.MateChatViewModelFactoryQualifier
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
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        mateChatsUseCase: MateChatUseCase
    ): ViewModelProvider.Factory {
        return MateChatViewModelFactory(localErrorDataSource, mateChatsUseCase)
    }
}