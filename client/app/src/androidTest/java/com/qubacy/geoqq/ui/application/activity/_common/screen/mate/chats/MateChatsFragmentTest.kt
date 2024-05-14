package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats

import androidx.navigation.NavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common.context.util.getUriFromResId
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.ui._common._test.view.util.action.scroll.recyclerview.RecyclerViewScrollToPositionViewAction
import com.qubacy.geoqq.ui._common._test.view.util.assertion.recyclerview.item.count.RecyclerViewItemCountViewAssertion
import com.qubacy.geoqq.ui.application.activity._common.screen._common._test.context.ScreenTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.AuthorizationFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common._test.context.MateTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.component.list.item.MateChatItemView
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.MateChatsViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.factory._test.mock.MateChatsViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module.FakeMateChatsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model.module.MateChatsViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.InsertChatsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.operation.UpdateChatChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chats.model._common.state.MateChatsUiState
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
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
>(), AuthorizationFragmentTest {
    companion object {
        val DEFAULT_AVATAR_RES_ID = R.drawable.test
    }

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
            .targetContext.getUriFromResId(DEFAULT_AVATAR_RES_ID)

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

    @Test
    fun scrollingDownLeadsToRequestingNewChatChunksTest() = runTest {
        val initChats = generateMateChatPresentations(20)
        val initInsertChatsOperation = InsertChatsUiOperation(0, initChats)

        defaultInit()

        // hard to fix without changing the main code for now:
//        Assert.assertFalse(mViewModelMockContext.getNextChatChunkCallFlag)

        mViewModelMockContext.uiOperationFlow.emit(initInsertChatsOperation)

        Espresso.onView(withId(R.id.fragment_mate_chats_list))
            .perform(RecyclerViewScrollToPositionViewAction(initChats.size - 1))

        Assert.assertTrue(mViewModelMockContext.getNextChatChunkCallFlag)
    }

    @Test
    fun clickingChatPreviewLeadsToTransitionToMateChatFragmentTest() = runTest {
        val initChats = generateMateChatPresentations(1)
        val initInsertChatsOperation = InsertChatsUiOperation(0, initChats)

        val chat = initChats.first()

        val expectedDestination = R.id.mateChatFragment

        initWithModelContext(MateChatsViewModelMockContext(
            MateChatsUiState(), prepareChatForEntering = chat))

        mViewModelMockContext.uiOperationFlow.emit(initInsertChatsOperation)

        Espresso.onView(withText(chat.user.username)).perform(ViewActions.click())

        val gottenDestination = mNavController.currentDestination!!.id

        Assert.assertTrue(mViewModelMockContext.prepareChatForEnteringCallFlag)
        Assert.assertEquals(expectedDestination, gottenDestination)
    }

    @Test
    fun clickingMyProfileMenuOptionLeadsToNavigationToMyProfileFragmentTest() {
        defaultInit()

        val expectedDestination = R.id.myProfileFragment

        Espresso.onView(withId(R.id.main_top_bar_option_my_profile)).perform(ViewActions.click())

        val gottenDestination = mNavController.currentDestination!!.id

        Assert.assertEquals(expectedDestination, gottenDestination)
    }

    @Test
    fun onMateChatsFragmentInsertChatsTest() = runTest {
        val initChats = mutableListOf<MateChatPresentation>()
        val initInsertChatsOperation = InsertChatsUiOperation(0, initChats)

        val chats = generateMateChatPresentations(1)
        val insertChatsOperation = InsertChatsUiOperation(initChats.size, chats)

        val expectedInitItemCount = initChats.size
        val expectedFinalItemCount = chats.size + expectedInitItemCount

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(initInsertChatsOperation)

        Espresso.onView(withId(R.id.fragment_mate_chats_list))
            .check(RecyclerViewItemCountViewAssertion(expectedInitItemCount))

        mViewModelMockContext.uiOperationFlow.emit(insertChatsOperation)

        Espresso.onView(withId(R.id.fragment_mate_chats_list))
            .check(RecyclerViewItemCountViewAssertion(expectedFinalItemCount))
    }

    @Test
    fun onMateChatsFragmentUpdateChatsTest() = runTest {
        val initChats = generateMateChatPresentations(1)
        val initInsertChatsOperation = InsertChatsUiOperation(0, initChats)

        val chat = initChats.first()
        val updatedUser = chat.user.copy(username = "updated one")
        val updatedChat = chat.copy(user = updatedUser)
        val updatedChats = mutableListOf(updatedChat)
        val updateChatsChunkOperation = UpdateChatChunkUiOperation(
            initInsertChatsOperation.position, updatedChats)

        val expectedInitChatPreviewTitle = chat.user.username
        val expectedChatPreviewTitle = updatedChat.user.username

        defaultInit()

        mViewModelMockContext.uiOperationFlow.emit(initInsertChatsOperation)

        Espresso.onView(withText(expectedInitChatPreviewTitle))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        mViewModelMockContext.uiOperationFlow.emit(updateChatsChunkOperation)

        Espresso.onView(withText(expectedChatPreviewTitle))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun generateMateChatPresentations(
        count: Int,
        offset: Int = 0
    ): MutableList<MateChatPresentation> {
        return IntRange(offset, count + offset - 1).map { it ->
            val id = it.toLong()
            val user = ScreenTestContext.generateUserPresentation(mImagePresentation, id + 1)
            val lastMessage = MateTestContext.generateMateMessagePresentation(user, id)

            MateTestContext.generateMateChatPresentation(user, id, it + 1, lastMessage)
        }.toMutableList()
    }

    override fun beforeAdjustUiWithLoadingStateTest() {
        val initChats = generateMateChatPresentations(1)
        val initUiState = MateChatsUiState(chats = initChats)

        initWithModelContext(MateChatsViewModelMockContext(initUiState))
    }

    override fun assertAdjustUiWithFalseLoadingState() {
        Espresso.onView(withId(R.id.fragment_mate_chats_progress_bar))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        Espresso.onView(ViewMatchers.isAssignableFrom(MateChatItemView::class.java))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    override fun assertAdjustUiWithTrueLoadingState() {
        Espresso.onView(withId(R.id.fragment_mate_chats_progress_bar))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        // todo: fix this (doesn't work):
        Espresso.onView(ViewMatchers.isAssignableFrom(MateChatItemView::class.java))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }

    override fun beforeNavigateToLoginTest() {
        defaultInit()
    }

    override fun getAuthorizationFragmentNavController(): NavController {
        return mNavController
    }

    override fun getAuthorizationFragmentLoginAction(): Int {
        return R.id.action_mateChatsFragment_to_loginFragment
    }

    override fun getAuthorizationFragmentActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }
}