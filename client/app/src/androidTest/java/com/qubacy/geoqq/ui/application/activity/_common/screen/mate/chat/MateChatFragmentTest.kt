package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.databinding.FragmentMateChatBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.factory._test.mock.MateChatViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.module.FakeMateChatViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.module.MateChatViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.state.MateChatUiState
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common.context.util.getUriFromResId
import com.qubacy.geoqq.ui._common._test.view.util.matcher.toolbar.layout.collapsing.CollapsingToolbarLayoutTitleViewMatcher
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(MateChatViewModelModule::class)
@RunWith(AndroidJUnit4::class)
class MateChatsFragmentTest : BusinessFragmentTest<
    FragmentMateChatBinding,
    MateChatUiState,
    MateChatViewModel,
    MateChatViewModelMockContext,
    MateChatFragment
>() {
    private lateinit var mChatPresentation: MateChatPresentation

    override fun setup() {
        super.setup()

        val imageUri = InstrumentationRegistry.getInstrumentation()
            .targetContext.getUriFromResId(R.drawable.ic_launcher_background)
        val imagePresentation = ImagePresentation(0, imageUri)
        val userPresentation = UserPresentation(
            0, "test", "test", imagePresentation, true, false)

        mChatPresentation = MateChatPresentation(0, userPresentation, 0, null)
    }

    override fun getPermissionsToGrant(): Array<String> {
        return super.getPermissionsToGrant().plus(arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
    }

    override fun createDefaultViewModelMockContext(): MateChatViewModelMockContext {
        return MateChatViewModelMockContext(MateChatUiState())
    }

    override fun attachViewModelMockContext() {
        FakeMateChatViewModelModule.mockContext = mViewModelMockContext
    }

    override fun getFragmentClass(): Class<MateChatFragment> {
        return MateChatFragment::class.java
    }

    override fun getCurrentDestination(): Int {
        return R.id.mateChatFragment
    }

    @Test
    fun uiSetAccordingToChatContextTest() {
        val chatContext = mChatPresentation

        mNavArgs = MateChatFragmentArgs(chatContext).toBundle()

        initWithModelContext(
            MateChatViewModelMockContext(uiState = MateChatUiState(chatContext = chatContext)))

        Espresso.onView(withId(R.id.fragment_mate_chat_top_bar_content_wrapper))
            .check(ViewAssertions.matches(
                CollapsingToolbarLayoutTitleViewMatcher(chatContext.user.username)))
    }
}