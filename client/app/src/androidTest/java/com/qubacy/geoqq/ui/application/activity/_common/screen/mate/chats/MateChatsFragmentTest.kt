package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats

import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common.context.util.getUriFromResId
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.ui._common._test.view.util.action.scroll.recyclerview.RecyclerViewScrollToPositionViewAction
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user.UserPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.user._test.util.UserPresentationGenerator
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory._test.mock.MateChatsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module.FakeMateChatsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module.MateChatsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.state.MateChatsUiState
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert
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
    private lateinit var mImagePresentation: ImagePresentation

    override fun getPermissionsToGrant(): Array<String> {
        return super.getPermissionsToGrant().plus(arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ))
    }

    override fun setup() {
        super.setup()

        val imageUri = InstrumentationRegistry.getInstrumentation()
            .targetContext.getUriFromResId(R.drawable.ic_launcher_background)

        mImagePresentation = ImagePresentation(0, imageUri)
    }

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
    fun tryingToLoadChatsOnEmptyChatChunkMapTest() {
        val initUiState = MateChatsUiState(chats = mutableListOf())

        initWithModelContext(MateChatsViewModelMockContext(uiState = initUiState))

        Assert.assertTrue(mViewModelMockContext.getNextChatChunkCallFlag)
    }

    // todo: fix this one (not passing for now):
    @Test
    fun scrollingDownLeadsToRequestingNewChatChunksTest() {
        val initChats = generateMateChatPresentations(20)
        val initUiState = MateChatsUiState(chats = initChats)

        initWithModelContext(MateChatsViewModelMockContext(uiState = initUiState))

        Assert.assertFalse(mViewModelMockContext.getNextChatChunkCallFlag)

        Espresso.onView(withId(R.id.fragment_mate_chats_list))
            .perform(RecyclerViewScrollToPositionViewAction(initChats.size - 1))

        Assert.assertTrue(mViewModelMockContext.getNextChatChunkCallFlag)
    }

    private fun generateMateChatPresentations(
        count: Int,
        offset: Int = 0
    ): MutableList<MateChatPresentation> {
        return IntRange(offset, count + offset).map { it ->
            val id = it.toLong()
            val user = UserPresentationGenerator.generateUserPresentation(id, mImagePresentation)

            MateChatPresentation(
                id,
                user,
                it + 1,
                generateLastMessagePresentation(id, user)
            )
        }.toMutableList()
    }

    private fun generateLastMessagePresentation(
        chatId: Long,
        user: UserPresentation
    ): MateMessagePresentation {
        return MateMessagePresentation(
            0, user, "test in chat $chatId", "timestamp"
        )
    }
}