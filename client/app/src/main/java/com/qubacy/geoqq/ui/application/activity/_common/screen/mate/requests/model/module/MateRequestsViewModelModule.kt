package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.impl.MateRequestsViewModelImplFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.impl.MateRequestsViewModelFactoryQualifier
import dagger.Binds
import dagger.Module

@Module
abstract class MateRequestsViewModelModule {
    @Binds
    @MateRequestsViewModelFactoryQualifier
    abstract fun bindMateRequestsViewModel(
        mateRequestsViewModelFactory: MateRequestsViewModelImplFactory
    ): ViewModelProvider.Factory
}