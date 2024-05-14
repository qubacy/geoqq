package com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat

import androidx.navigation.NavController
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.qubacy.geoqq.databinding.FragmentMateChatBinding
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.base.business.BusinessFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.factory._test.mock.MateChatViewModelMockContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.module.FakeMateChatViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model.module.MateChatViewModelModule
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.state.MateChatUiState
import com.qubacy.geoqq.R
import com.qubacy.geoqq._common.context.util.getUriFromResId
import com.qubacy.geoqq.ui._common._test.view.util.action.click.soft.SoftClickViewAction
import com.qubacy.geoqq.ui._common._test.view.util.action.scroll.recyclerview.RecyclerViewScrollToPositionViewAction
import com.qubacy.geoqq.ui._common._test.view.util.action.wait.WaitViewAction
import com.qubacy.geoqq.ui._common._test.view.util.assertion.recyclerview.item.count.RecyclerViewItemCountViewAssertion
import com.qubacy.geoqq.ui._common._test.view.util.matcher.toolbar.layout.collapsing.CollapsingToolbarLayoutTitleViewMatcher
import com.qubacy.geoqq.ui.application.activity._common.screen._common._test.context.ScreenTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.authorized.AuthorizationFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.chat.ChatFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.InterlocutorFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.interlocutor.model.operation.ShowInterlocutorDetailsUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen._common.fragment.aspect.popup.PopupFragmentTest
import com.qubacy.geoqq.ui.application.activity._common.screen._common.presentation.image.ImagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common._test.context.MateTestContext
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateChatPresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate._common.presentation.MateMessagePresentation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.MateChatViewModel
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.context.ChatContextUpdatedUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.message.InsertMessagesUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.message.UpdateMessageChunkUiOperation
import com.qubacy.geoqq.ui.application.activity._common.screen.mate.chat.model._common.operation.request.ChatDeletedUiOperation
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(MateChatViewModelModule::class)
@RunWith(AndroidJUnit4::class)
class MateChatFragmentTest : BusinessFragmentTest<
    FragmentMateChatBinding,
    MateChatUiState,
    MateChatViewModel,
    MateChatViewModelMockContext,
    MateChatFragment
>(),
    AuthorizationFragmentTest,
    InterlocutorFragmentTest<MateChatFragment>,
    ChatFragmentTest<MateChatFragment>,
    PopupFragmentTest<MateChatFragment>
{
    companion object {
        val DEFAULT_AVATAR_RES_ID = R.drawable.test
    }

    private lateinit var mImagePresentation: ImagePresentation
    private lateinit var mChatPresentation: MateChatPresentation

    override fun setup() {
        val imageUri = InstrumentationRegistry.getInstrumentation()
            .targetContext.getUriFromResId(DEFAULT_AVATAR_RES_ID)

        mImagePresentation = ImagePresentation(0, imageUri)

        val userPresentation = ScreenTestContext.generateUserPresentation(mImagePresentation)

        mChatPresentation = MateTestContext.generateMateChatPresentation(userPresentation)

        super.setup()
    }

    override fun defaultInit() {
        initWithModelContext(
            MateChatViewModelMockContext(
            uiState = MateChatUiState(chatContext = mChatPresentation)
            )
        )
    }

    override fun initDefaultNavArgs() {
        mNavArgs = MateChatFragmentArgs(mChatPresentation).toBundle()
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

    @Test
    fun userDetailsCardOpensOnClickingInterlocutorProfileMenuItemTest() = runTest {
        val chatContext = mChatPresentation.copy(user = mChatPresentation.user.copy(isMate = true))

        mNavArgs = MateChatFragmentArgs(chatContext).toBundle()

        initWithModelContext(
            MateChatViewModelMockContext(uiState = MateChatUiState(chatContext = chatContext)))

        mViewModelMockContext.uiOperationFlow
            .emit(ShowInterlocutorDetailsUiOperation(chatContext.user))

        Espresso.onView(withId(R.id.component_bottom_sheet_user_container))
            .check(ViewAssertions.matches(Matchers.allOf(
//                ViewMatchers.hasDescendant(Matchers.allOf(
//                    withId(R.id.component_bottom_sheet_user_image_avatar),
//                    CommonImageViewMatcher(chatContext.user.avatar.uri)
//                )),
                ViewMatchers.hasDescendant(withText(chatContext.user.username)),
                ViewMatchers.hasDescendant(withText(chatContext.user.description)),
                ViewMatchers.hasDescendant(withText(
                    R.string.component_bottom_sheet_user_button_mate_caption_remove))
            )))
    }

    @Test
    fun mateButtonHasDifferentCaptionOnInterlocutorChangingTest() = runTest {
        val chatContext = mChatPresentation

        mNavArgs = MateChatFragmentArgs(chatContext).toBundle()

        initWithModelContext(
            MateChatViewModelMockContext(uiState = MateChatUiState(chatContext = chatContext)))

        val interlocutorNotMate = mChatPresentation.user.copy(isMate = false)
        val interlocutorMate = mChatPresentation.user.copy(isMate = true)

        val expectedInterlocutorNotMateButtonCaption =
            R.string.component_bottom_sheet_user_button_mate_caption_add
        val expectedInterlocutorMateButtonCaption =
            R.string.component_bottom_sheet_user_button_mate_caption_remove

        mViewModelMockContext.uiOperationFlow.emit(
            ShowInterlocutorDetailsUiOperation(interlocutorNotMate)
        )

        Espresso.onView(withId(R.id.component_bottom_sheet_user_container))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText(
                expectedInterlocutorNotMateButtonCaption))))

        mViewModelMockContext.uiOperationFlow.emit(
            ShowInterlocutorDetailsUiOperation(interlocutorMate)
        )

        Espresso.onView(withId(R.id.component_bottom_sheet_user_container))
            .check(ViewAssertions.matches(ViewMatchers.hasDescendant(withText(
                expectedInterlocutorMateButtonCaption))))
    }

    @Test
    fun requestDialogAppearsOnClickingDeleteChatMenuOptionTest() {
        val initInterlocutor = mChatPresentation.user.copy(isDeleted = true)
        val chatContext = mChatPresentation.copy(user = initInterlocutor)

        mNavArgs = MateChatFragmentArgs(chatContext).toBundle()

        initWithModelContext(
            MateChatViewModelMockContext(
                uiState = MateChatUiState(chatContext = chatContext), isChatDeletable = true))

        Espresso.onView(withId(R.id.mate_chat_top_bar_option_delete_chat))
            .perform(ViewActions.click())
        Espresso.onView(withText(R.string.fragment_mate_chat_dialog_request_delete_chat_confirmation))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun requestDialogAppearsOnClickingDeleteFromMatesButtonTest() = runTest {
        val initInterlocutor = mChatPresentation.user.copy(isDeleted = false, isMate = true)
        val chatContext = mChatPresentation.copy(user = initInterlocutor)

        mNavArgs = MateChatFragmentArgs(chatContext).toBundle()

        initWithModelContext(
            MateChatViewModelMockContext(
                uiState = MateChatUiState(chatContext = chatContext),
                isInterlocutorMate = true,
                isInterlocutorMateableOrDeletable = true))

        mViewModelMockContext.uiOperationFlow.emit(
            ShowInterlocutorDetailsUiOperation(initInterlocutor)
        )

        Espresso.onView(withId(R.id.component_bottom_sheet_user_button_mate))
            .perform(SoftClickViewAction()) // todo: it's used here 'cause the normal one doesn't work as we use MotionLayout;
        Espresso.onView(withText(R.string.fragment_mate_chat_dialog_request_delete_chat_confirmation))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun scrollingUpLeadsToRequestingNewMessageChunkTest() = runTest {
        val chatContext = mChatPresentation
        val messages = generateMateMessagePresentations(20)
        val initUiState = MateChatUiState(chatContext = chatContext)

        initWithModelContext(MateChatViewModelMockContext(uiState = initUiState))

        mViewModelMockContext.uiOperationFlow.emit(InsertMessagesUiOperation(0, messages))

        Espresso.onView(withId(R.id.fragment_mate_chat_list))
            .perform(RecyclerViewScrollToPositionViewAction(messages.size - 1))

        Assert.assertTrue(mViewModelMockContext.getNextMessageChunkCallFlag)
    }

    @Test
    fun onMateChatFragmentInsertMessagesTest() = runTest {
        val chatContext = mChatPresentation
        val messages = generateMateMessagePresentations(20)
        val initUiState = MateChatUiState(chatContext = chatContext)

        initWithModelContext(MateChatViewModelMockContext(uiState = initUiState))

        val expectedMessageListItemCount = messages.size

        mViewModelMockContext.uiOperationFlow.emit(InsertMessagesUiOperation(0, messages))

        Espresso.onView(withId(R.id.fragment_mate_chat_list))
            .check(RecyclerViewItemCountViewAssertion(expectedMessageListItemCount))
    }

    @Test
    fun onMateChatFragmentUpdateMessagesTest() = runTest {
        val chatContext = mChatPresentation
        val initMessages = generateMateMessagePresentations(20)
        val messagePosition = 0
        val initUiState = MateChatUiState(chatContext = chatContext)

        initWithModelContext(MateChatViewModelMockContext(uiState = initUiState))

        mViewModelMockContext.uiOperationFlow.emit(
            InsertMessagesUiOperation(messagePosition, initMessages)
        )

        val updatedMessages = generateMateMessagePresentations(5)
        val messageChunkSizeDelta = initMessages.size - updatedMessages.size

        val expectedMessageListItemCount = updatedMessages.size

        mViewModelMockContext.uiOperationFlow.emit(
            UpdateMessageChunkUiOperation(messagePosition, updatedMessages, messageChunkSizeDelta)
        )

        Espresso.onView(withId(R.id.fragment_mate_chat_list))
            .check(RecyclerViewItemCountViewAssertion(expectedMessageListItemCount))
    }

    /**
     * Fails due to synchronization issues;
     */
    @Test
    fun onMateChatFragmentChatDeletedTest() = runTest {
        val chatDeletedUiOperation = ChatDeletedUiOperation()

        val expectedDestinationId = R.id.mateChatsFragment

        initWithDefaultChatContext()

        mViewModelMockContext.uiOperationFlow.emit(chatDeletedUiOperation)

        val gottenDestinationId = mNavController.currentDestination!!.id

        Assert.assertEquals(expectedDestinationId, gottenDestinationId)
    }

    @Test
    fun onMateChatFragmentChatContextUpdatedTest() = runTest {
        val initInterlocutor = mChatPresentation.user.copy(isDeleted = false, isMate = true)
        val initChatContext = mChatPresentation.copy(user = initInterlocutor)

        val interlocutor = initInterlocutor.copy(isDeleted = true)
        val chatContext = initChatContext.copy(user = interlocutor)

        mNavArgs = MateChatFragmentArgs(initChatContext).toBundle()

        initWithModelContext(MateChatViewModelMockContext(
            MateChatUiState(chatContext = initChatContext)))

        Espresso.onView(withId(R.id.mate_chat_top_bar_option_delete_chat))
            .check(ViewAssertions.doesNotExist())

        mViewModelMockContext.isChatDeletable = true

        mViewModelMockContext.uiOperationFlow.emit(ChatContextUpdatedUiOperation(chatContext))

        Espresso.onView(withId(R.id.mate_chat_top_bar_option_delete_chat))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    // todo: not possible for now:
//    @Test
//    fun modelSetChatContextCalledTest() {
//        val chatContext = mChatPresentation
//
//        mNavArgs = MateChatFragmentArgs(chatContext).toBundle()
//
//        initWithModelContext(MateChatViewModelMockContext(MateChatUiState()))
//
//        Assert.assertTrue(mViewModelMockContext.setChatContextCallFlag)
//    }

    @Test
    fun modelGetInterlocutorProfileCalledTest() {
        val chatContext = mChatPresentation
        val initUiState = MateChatUiState(chatContext = chatContext)

        initWithModelContext(MateChatViewModelMockContext(
            uiState = initUiState, getInterlocutorProfile = chatContext.user
        ))

        Espresso.onView(withId(R.id.mate_chat_top_bar_option_show_mate_profile))
            .perform(ViewActions.click())

        Assert.assertTrue(mViewModelMockContext.getInterlocutorProfileCallFlag)
    }

    @Test
    fun modelAddInterlocutorAsMateCalledTest() = runTest {
        val interlocutor = mChatPresentation.user.copy(isMate = false)
        val chatContext = mChatPresentation.copy(user = interlocutor)
        val initUiState = MateChatUiState(chatContext = chatContext)

        initWithModelContext(MateChatViewModelMockContext(uiState = initUiState))

        mViewModelMockContext.uiOperationFlow.emit(ShowInterlocutorDetailsUiOperation(interlocutor))

        Espresso.onView(withId(R.id.component_bottom_sheet_user_button_mate))
            .perform(SoftClickViewAction())

        Assert.assertTrue(mViewModelMockContext.addInterlocutorAsMateCallFlag)
    }

    @Test
    fun modelDeleteChatCalledTest() {
        val chatContext = mChatPresentation
        val initUiState = MateChatUiState(chatContext = chatContext)

        initWithModelContext(MateChatViewModelMockContext(
            uiState = initUiState, isChatDeletable = true))

        Espresso.onView(withId(R.id.mate_chat_top_bar_option_delete_chat))
            .perform(ViewActions.click())
        Espresso.onView(Matchers.allOf(
            withText(R.string.component_request_dialog_button_positive_caption) // todo: specify;
        )).perform(ViewActions.click())

        Assert.assertTrue(mViewModelMockContext.deleteChatCallFlag)
    }

    private fun generateMateMessagePresentations(
        count: Int
    ): MutableList<MateMessagePresentation> {
        return IntRange(0, count - 1).map {
            val id = it.toLong()
            val user = ScreenTestContext.generateUserPresentation(mImagePresentation)

            MateMessagePresentation(id, user, "test $id", "TEST")
        }.toMutableList()
    }

    override fun beforePopupMessageOccurredTest() {
        defaultInit()
    }

    override fun getPopupActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }

    override fun getPopupFragment(): MateChatFragment {
        return mFragment
    }

    override fun beforeAdjustUiWithLoadingStateTest() {
        val chatContext = mChatPresentation
            .copy(user = mChatPresentation.user.copy(isMate = true, isDeleted = false))

        mNavArgs = MateChatFragmentArgs(chatContext).toBundle()

        initWithModelContext(MateChatViewModelMockContext(
            MateChatUiState(chatContext = chatContext), isInterlocutorChatable = true))
    }

    override fun assertAdjustUiWithFalseLoadingState() {
        Espresso.onView(withId(R.id.fragment_mate_chat_progress_bar))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
        Espresso.onView(withId(R.id.fragment_mate_chat_input_message))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    override fun assertAdjustUiWithTrueLoadingState() {
        Espresso.onView(withId(R.id.fragment_mate_chat_progress_bar))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.fragment_mate_chat_input_message))
            .check(ViewAssertions.matches(ViewMatchers.isNotEnabled()))
    }

    override fun beforeNavigateToLoginTest() {
        initWithDefaultChatContext()
    }

    override fun getAuthorizationFragmentNavController(): NavController {
        return mNavController
    }

    override fun getAuthorizationFragmentActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }

    override fun getAuthorizationFragmentLoginAction(): Int {
        return R.id.action_mateChatFragment_to_loginFragment
    }

    override fun onChatFragmentMessageSentTest() {
        // todo: nothing to check for now..

    }

    override fun beforeOnChatFragmentMateRequestSentTest() {
        initWithDefaultChatContext()
    }

    override fun getChatFragmentFragment(): MateChatFragment {
        return mFragment
    }

    override fun getChatFragmentInterlocutorFragmentTest(): InterlocutorFragmentTest<MateChatFragment> {
        return this
    }

    override fun beforeAdjustInterlocutorFragmentUiWithInterlocutorTest() {
        initWithDefaultChatContext()
    }

    override fun beforeOpenInterlocutorDetailsSheetTest() {
        initWithDefaultChatContext()
    }

    override fun getInterlocutorFragmentFragment(): MateChatFragment {
        return mFragment
    }

    override fun getInterlocutorFragmentActivityScenario(): ActivityScenario<*> {
        return mActivityScenario
    }

    override fun getInterlocutorFragmentAvatar(): ImagePresentation {
        return mImagePresentation
    }

    private fun initWithDefaultChatContext() {
        val chatContext = mChatPresentation
        val initUiState = MateChatUiState(chatContext = chatContext)

        initWithModelContext(MateChatViewModelMockContext(uiState = initUiState))
    }
}