package com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.module.FakeViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.impl.GeoChatViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.factory.FakeGeoChatViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model.factory._test.mock.GeoChatViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.geo.chat.model._common.state.GeoChatUiState
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [ActivityRetainedComponent::class],
    replaces = [GeoChatViewModelModule::class]
)
object FakeGeoChatViewModelModule : FakeViewModelModule<
    GeoChatUiState, GeoChatViewModelMockContext
>() {
    @Provides
    @GeoChatViewModelFactoryQualifier
    fun provideFakeGeoChatViewModelFactory(): ViewModelProvider.Factory {
        return FakeGeoChatViewModelFactory(mockContext)
    }
}