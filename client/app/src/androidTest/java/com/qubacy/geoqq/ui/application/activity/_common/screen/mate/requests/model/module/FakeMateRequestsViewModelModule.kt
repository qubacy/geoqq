package com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.module.FakeViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.impl.MateRequestsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.factory.FakeMateRequestsViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model.factory._test.mock.MateRequestsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.requests.model._common.state.MateRequestsUiState
import dagger.Module
import dagger.Provides

@Module
object FakeMateRequestsViewModelModule : FakeViewModelModule<
    MateRequestsUiState, MateRequestsViewModelMockContext
>() {
    @Provides
    @MateRequestsViewModelFactoryQualifier
    fun provideFakeMateRequestsViewModelFactory(): ViewModelProvider.Factory {
        return FakeMateRequestsViewModelFactory(mockContext)
    }
}