package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._di.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.impl.GeoChatViewModelImplFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.impl.GeoChatViewModelFactoryQualifier
import dagger.Binds
import dagger.Module

@Module
abstract class GeoChatViewModelModule {
    @Binds
    @GeoChatViewModelFactoryQualifier
    abstract fun bindGeoChatViewModelFactory(
        geoChatViewModelFactory: GeoChatViewModelImplFactory
    ): ViewModelProvider.Factory
}