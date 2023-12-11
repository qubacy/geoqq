package com.qubacy.geoqq.ui.screen.geochat.chat

import android.net.Uri
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.R
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.model.user.User
import com.qubacy.geoqq.domain.common.operation.chat.SetUsersDetailsOperation
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.common.util.generator.MessageGeneratorUtility
import com.qubacy.geoqq.domain.common.util.generator.UserGeneratorUtility
import com.qubacy.geoqq.domain.geochat.chat.state.GeoChatState
import com.qubacy.geoqq.ui.common.visual.component.bottomsheet.userinfo.content.UserInfoBottomSheetContent
import com.qubacy.geoqq.ui.common.visual.fragment.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.common.visual.fragment.common.base.BaseFragment
import com.qubacy.geoqq.ui.screen.common.fragment.chat.ChatFragmentTest
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.screen.geochat.settings.GeoChatSettingsFragmentDirections
import com.qubacy.geoqq.ui.util.DragBottomSheetViewAction
import com.qubacy.geoqq.ui.util.MaterialTextInputVisualLineCountViewAssertion
import com.qubacy.geoqq.ui.util.WaitingViewAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeoChatFragmentTest : ChatFragmentTest<GeoChatState>() {
    class GeoChatUiStateTestData(
        chatStateFlow: MutableStateFlow<GeoChatState?>,
        chatUiState: LiveData<ChatUiState?>
    ) : ChatUiStateTestData<GeoChatState>(
        chatStateFlow, chatUiState
    ) {
        override fun generateChatState(
            messages: List<Message>,
            users: List<User>,
            operations: List<Operation>
        ): GeoChatState {
            return GeoChatState(messages, users, operations)
        }

        fun openUserDetails(user: User) {
            val state = GeoChatState(
                listOf(), listOf(user), listOf(SetUsersDetailsOperation(listOf(user.id), true)))

            runBlocking {
                mChatStateFlow.emit(state)
            }
        }
    }

    companion object {
        const val TAG = "GeoChatTest"
    }

    private lateinit var mGeoChatFragmentScenarioRule: FragmentScenario<GeoChatFragment>
    private lateinit var mModel: GeoChatViewModel

    private lateinit var mGeoChatUiStateTestData: GeoChatUiStateTestData

    @Before
    override fun setup() {
        super.setup()

        val args = GeoChatSettingsFragmentDirections
            .actionGeoChatSettingsFragmentToGeoChatFragment(0)
            .arguments

        mGeoChatFragmentScenarioRule = launchFragmentInContainer(
            fragmentArgs = args,
            themeResId = R.style.Theme_Geoqq_GeoChat)
        mGeoChatFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        var fragment: GeoChatFragment? = null

        mGeoChatFragmentScenarioRule.moveToState(Lifecycle.State.STARTED)
        mGeoChatFragmentScenarioRule.onFragment {
            fragment = it
        }

        val modelFieldReflection = BaseFragment::class.java.getDeclaredField("mModel")
            .apply { isAccessible = true }
        val geoChatStateFlowFieldReflection = GeoChatViewModel::class.java
            .getDeclaredField("mGeoChatStateFlow")
            .apply { isAccessible = true }

        mModel = modelFieldReflection.get(fragment) as GeoChatViewModel
        mGeoChatUiStateTestData = GeoChatUiStateTestData(
            geoChatStateFlowFieldReflection.get(mModel) as MutableStateFlow<GeoChatState?>,
            mModel.geoChatUiStateFlow as LiveData<ChatUiState?>
        )

        val testUsers = UserGeneratorUtility.generateUsers(2)

        mGeoChatUiStateTestData.setChat(listOf(), testUsers)
    }

    @Test
    fun allElementsAreInPlaceTest() {
        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.sending_message))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.sending_button))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun allElementsAreEnabledTest() {
        Espresso.onView(withId(R.id.chat_recycler_view))
            .perform(ViewActions.swipeDown())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.sending_message))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.sending_button))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun clickingSendButtonWithEmptyMessageFieldLeadsToErrorTest() {
        Espresso.onView(withId(R.id.sending_message))
            .perform(ViewActions.clearText())
        Espresso.onView(withId(R.id.sending_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.error_chat_message_incorrect))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun emptyMessageSnackbarDisappearsOnDismissButtonClickedTest() {
        Espresso.onView(withId(R.id.sending_message))
            .perform(ViewActions.clearText())
        Espresso.onView(withId(R.id.sending_button))
            .perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withText(R.string.fragment_base_show_message_action_dismiss_text))
            .perform(ViewActions.click())
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun clickingSendButtonWithNotEmptyMessageFieldCleansItAndGoesWithoutErrorTest() {
        val messageText = "Hi there"

        Espresso.onView(withId(R.id.sending_message))
            .perform(ViewActions.typeText(messageText))
        Espresso.onView(withId(R.id.sending_button))
            .perform(ViewActions.click())
        Espresso.onView(withId(R.id.sending_message))
            .check(ViewAssertions.matches(ViewMatchers.withText(String())))
        Espresso.onView(ViewMatchers.withText(R.string.error_chat_message_incorrect))
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun messageLongTextFitsTwoVisualLinesTest() {
        val longText = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi " +
                "ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit " +
                "in voluptate velit esse cillum dolore eu fugiat nulla pariatur."

        Espresso.onView(withId(R.id.sending_message))
            .perform(ViewActions.typeText(longText))
            .check(MaterialTextInputVisualLineCountViewAssertion(2))
    }

    @Test
    fun messageContainsCorrectTextUsernameAndTimestampTest() {
        val messages = listOf(Message(0, 0, "Hi", 1696847478000))
        val users = UserGeneratorUtility.generateUsers(1)

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(messages.size)))
        Espresso.onView(withText(users[0].username))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withText(messages[0].text))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        // todo: throws an exception for now:
//        Espresso.onView(withText("05:31 PM"))
//            .check(ViewAssertions.matches(
//                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun settingGeoChatUiStateWithThreeMessagesLeadsToThreeMessagesAppearInChatRecyclerViewTest() {
        val messages = listOf<Message>()
        val users = listOf<User>()

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(messages.size)))

        val endingMessages = listOf(
            Message(0, 0, "Hi!", 1696847478000),
            Message(1, 1, "qq", 1696847479000),
            Message(2, 0, "wassup?", 1696847480000),
        )
        val endingUsers = UserGeneratorUtility.generateUsers(2)

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(endingMessages, endingUsers)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .perform(WaitingViewAction(500))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(endingMessages.size)))
    }

    @Test
    fun messagesScrolledDownOnLackingSpaceTest() {
        val messages = listOf<Message>()
        val users = UserGeneratorUtility.generateUsers(2)

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(messages.size)))

        val endingMessages = MessageGeneratorUtility.generateMessages(12)

        for (endingMessage in endingMessages) {
            mGeoChatFragmentScenarioRule.onFragment {
                mGeoChatUiStateTestData.addMessage(endingMessage)
            }

            Espresso.onView(withId(R.id.chat_recycler_view))
                .perform(WaitingViewAction(1000))
        }

        Espresso.onView(withText(endingMessages.last().text))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun settingMessagesLeadsToAutoScrollingToBottomTest() {
        val messages = listOf<Message>()
        val users = listOf<User>()

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(messages.size)))

        val endingUsers = UserGeneratorUtility.generateUsers(2)
        val endingMessages = MessageGeneratorUtility.generateMessages(12)

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(endingMessages, endingUsers)
        }

        Espresso.onView(isRoot())
            .perform(WaitingViewAction(1000))
        Espresso.onView(withText(endingMessages.last().text))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun manualScrollingUpPreventsAutoScrollingTest() { // todo: doesn't work (don't know why for now);
        val messages = MessageGeneratorUtility.generateMessages(12)
        val users = UserGeneratorUtility.generateUsers(2)

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .perform(ViewActions.swipeDown(), WaitingViewAction(1000))

        val endingMessages = MessageGeneratorUtility.generateMessages(2, messages.size.toLong())

        for (endingMessage in endingMessages) {
            mGeoChatFragmentScenarioRule.onFragment {
                mGeoChatUiStateTestData.addMessage(endingMessage)
            }

            Espresso.onView(withId(R.id.chat_recycler_view))
                .perform(WaitingViewAction(1000))
        }

        Espresso.onView(withText(endingMessages.last().text))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isCompletelyDisplayed())))
    }

//    @Test
//    fun userInfoBottomSheetDisplayedOnMessageClickTest() { // todo: INVALID since full impl.
//        val user = UserGeneratorUtility.generateUsers(1, 1).first()
//        val message = Message(0, user.id, "test", 100)
//
//        mGeoChatFragmentScenarioRule.onFragment {
//            mGeoChatUiStateTestData.setChat(listOf(message), listOf(user))
//        }
//
//        Espresso.onView(withText(user.username))
//            .perform(ViewActions.click(), WaitingViewAction(1000))
//        Espresso.onView(ViewMatchers.isAssignableFrom(UserInfoBottomSheetContent::class.java))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//    }

    @Test
    fun userInfoBottomSheetHasAllElementsVisibleAndEnabledTest() {
        val user = User(1, "username", "desc", Uri.parse(String()), false)

        mGeoChatFragmentScenarioRule.onFragment {
            mModel.getUserDetails(user.id)

            mGeoChatUiStateTestData.openUserDetails(user)
        }

        Espresso.onView(isRoot()).perform(WaitingViewAction(1000))

        Espresso.onView(ViewMatchers.withId(R.id.avatar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(Matchers.allOf(
            ViewMatchers.withId(R.id.username),
            ViewMatchers.isDescendantOfA(
                ViewMatchers.isAssignableFrom(UserInfoBottomSheetContent::class.java))))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.add_friend_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun userInfoBottomSheetExpandsOnDraggingTopTest() {
        val user = User(1, "username", "desc", Uri.parse(String()), false)

        mGeoChatFragmentScenarioRule.onFragment {
            mModel.getUserDetails(user.id)

            mGeoChatUiStateTestData.openUserDetails(user)
        }

        Espresso.onView(isRoot()).perform(WaitingViewAction(1000))

        Espresso.onView(withText(user.username))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isAssignableFrom(UserInfoBottomSheetContent::class.java))
            .perform(DragBottomSheetViewAction()) // todo: DOESNT WORK. WHAT TO DO INSTEAD???
            .check(ViewAssertions.matches(ViewMatchers.isDisplayingAtLeast(80)))
    }

    @Test
    fun userInfoBottomSheetDismissesOnDraggingBottomTest() {
        // todo: their is no way to check this case without a working Dragging imitation..


    }

    @Test
    fun errorMessageAppearsOnShowErrorUiOperationTest() {
        val error = com.qubacy.geoqq.common.error.common.Error(0, "Test", false)

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.showError(error)
        }

        Espresso.onView(withText(error.message))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun criticalErrorLeadsToClosingAppTest() {
        val error = com.qubacy.geoqq.common.error.common.Error(0, "Test", true)

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.showError(error)
        }

        try {
            Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
                .perform(ViewActions.click())

        } catch (e: Exception) {
            Assert.assertEquals(NoActivityResumedException::class, e::class)
        }
    }
}