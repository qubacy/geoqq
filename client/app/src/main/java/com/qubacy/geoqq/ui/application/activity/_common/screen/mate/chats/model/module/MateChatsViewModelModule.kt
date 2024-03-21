package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.data.error.repository.ErrorDataRepository
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
        errorDataRepository: ErrorDataRepository,
        mateChatsUseCase: MateChatsUseCase
    ): ViewModelProvider.Factory {
        return MateChatsViewModelFactory(errorDataRepository, mateChatsUseCase)
    }
}