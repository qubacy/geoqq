package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data._common.repository._common.source.local.database.error.impl.LocalErrorDatabaseDataSourceImpl
import com.qubacy.geoqq.domain.mate.chats.usecase.MateChatsUseCase
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModelFactoryQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object MateChatsViewModelModule {
    @Provides
    @MateChatsViewModelFactoryQualifier
    fun provideMateChatsViewModel(
        localErrorDataSource: LocalErrorDatabaseDataSourceImpl,
        mateChatsUseCase: MateChatsUseCase
    ): ViewModelProvider.Factory {
        return MateChatsViewModelFactory(localErrorDataSource, mateChatsUseCase)
    }
}