package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.module.FakeViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.impl.GeoChatViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.factory.FakeGeoChatViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.factory._test.mock.GeoChatViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.state.GeoChatUiState
import dagger.Module
import dagger.Provides

@Module
object FakeGeoChatViewModelModule : FakeViewModelModule<
    GeoChatUiState, GeoChatViewModelMockContext
>() {
    @Provides
    @GeoChatViewModelFactoryQualifier
    fun provideFakeGeoChatViewModelFactory(): ViewModelProvider.Factory {
        return FakeGeoChatViewModelFactory(mockContext)
    }
}