package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module

import androidx.lifecycle.ViewModelProvider
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.stateful.model.module.FakeViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.impl.MateChatsViewModelFactoryQualifier
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory.FakeMateChatsViewModelFactory
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory._test.mock.MateChatsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.state.MateChatsUiState
import dagger.Module
import dagger.Provides

@Module
object FakeMateChatsViewModelModule : FakeViewModelModule<
    MateChatsUiState, MateChatsViewModelMockContext
>() {
    @Provides
    @MateChatsViewModelFactoryQualifier
    fun provideFakeMateChatsViewModelFactory(): ViewModelProvider.Factory {
        return FakeMateChatsViewModelFactory(mockContext)
    }
}