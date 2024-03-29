package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.R
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory._test.mock.MateChatsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module.FakeMateChatsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module.MateChatsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(MateChatsViewModelModule::class)
@RunWith(AndroidJUnit4::class)
class MateChatsFragmentTest : BusinessFragmentTest<
    FragmentMateChatsBinding,
    MateChatsUiState,
    MateChatsViewModel,
    MateChatsViewModelMockContext,
    MateChatsFragment
>() {
    override fun createDefaultViewModelMockContext(): MateChatsViewModelMockContext {
        return MateChatsViewModelMockContext(MateChatsUiState())
    }

    override fun attachViewModelMockContext() {
        FakeMateChatsViewModelModule.mockContext = mViewModelMockContext
    }

    override fun getFragmentClass(): Class<MateChatsFragment> {
        return MateChatsFragment::class.java
    }

    override fun getCurrentDestination(): Int {
        return R.id.mateChatsFragment
    }

    @Test
    fun test() {

    }
}