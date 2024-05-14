package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.module.FakeViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.impl.MateChatViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.factory.FakeMateChatViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.factory._test.mock.MateChatViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.state.MateChatUiState
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [ActivityRetainedComponent::class],
    replaces = [MateChatViewModelModule::class]
)
object FakeMateChatViewModelModule : FakeViewModelModule<
        MateChatUiState, MateChatViewModelMockContext
        >() {
    @Provides
    @MateChatViewModelFactoryQualifier
    fun provideFakeMateChatViewModelFactory(): ViewModelProvider.Factory {
        return FakeMateChatViewModelFactory(mockContext)
    }
}