package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._di.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.impl.MateChatViewModelImplFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.impl.MateChatViewModelFactoryQualifier
import dagger.Binds
import dagger.Module

@Module
abstract class MateChatViewModelModule {
    @Binds
    @MateChatViewModelFactoryQualifier
    abstract fun bindMateChatViewModel(
        mateChatViewModelFactory: MateChatViewModelImplFactory
    ): ViewModelProvider.Factory
}