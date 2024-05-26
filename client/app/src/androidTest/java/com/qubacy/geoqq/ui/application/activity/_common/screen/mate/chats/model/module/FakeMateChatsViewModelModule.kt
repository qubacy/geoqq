package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.module.FakeViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.impl.MateChatsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory.FakeMateChatsViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory._test.mock.MateChatsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.state.MateChatsUiState
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._di.module.MateChatsViewModelModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [ActivityRetainedComponent::class],
    replaces = [MateChatsViewModelModule::class]
)
object FakeMateChatsViewModelModule : FakeViewModelModule<
        MateChatsUiState, MateChatsViewModelMockContext
        >() {
    @Provides
    @MateChatsViewModelFactoryQualifier
    fun provideFakeMateChatsViewModelFactory(): ViewModelProvider.Factory {
        return FakeMateChatsViewModelFactory(mockContext)
    }
}