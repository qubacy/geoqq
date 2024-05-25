package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.impl.MateChatsViewModelImplFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.impl.MateChatsViewModelFactoryQualifier
import dagger.Binds
import dagger.Module

@Module
abstract class MateChatsViewModelModule {
    @Binds
    @MateChatsViewModelFactoryQualifier
    abstract fun bindMateChatsViewModel(
        mateChatsViewModelFactory: MateChatsViewModelImplFactory
    ): ViewModelProvider.Factory
}