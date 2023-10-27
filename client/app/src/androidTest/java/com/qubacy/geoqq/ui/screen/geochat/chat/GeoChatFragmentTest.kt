package com.qubacy.geoqq.ui.screen.geochat.chat

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModelProvider
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
import com.qubacy.geoqq.common.error.common.ErrorBase
import com.qubacy.geoqq.common.error.local.LocalError
import com.qubacy.geoqq.data.common.chat.state.ChatState
import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.ui.common.component.bottomsheet.userinfo.UserInfoBottomSheetContent
import com.qubacy.geoqq.ui.screen.common.chat.ChatFragmentTest
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapter
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.util.DragBottomSheetViewAction
import com.qubacy.geoqq.ui.util.MaterialTextInputVisualLineCountViewAssertion
import com.qubacy.geoqq.ui.util.SilentClickViewAction
import com.qubacy.geoqq.ui.util.WaitingViewAction
import kotlinx.coroutines.flow.MutableStateFlow
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class GeoChatFragmentTest : ChatFragmentTest() {
    companion object {
        const val TAG = "GeoChatTest"
    }

    private lateinit var mGeoChatFragmentScenarioRule: FragmentScenario<GeoChatFragment>
    private lateinit var mModel: GeoChatViewModel

    private lateinit var mGeoChatUiStateTestData: ChatUiStateTestData

    @Before
    fun setup() {
        mGeoChatFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_GeoChat)

        val adapterFieldReflection = GeoChatFragment::class.java
            .getDeclaredField("mGeoChatAdapter")
            .apply {
                isAccessible = true
            }

        var adapter: ChatAdapter? = null

        mGeoChatFragmentScenarioRule.onFragment {
            mModel = ViewModelProvider(it)[GeoChatViewModel::class.java]
            adapter = adapterFieldReflection.get(it) as ChatAdapter
        }

        val geoChatStateFlowFieldReflection = GeoChatViewModel::class.java
            .getDeclaredField("mGeoChatStateFlow")
            .apply {
                isAccessible = true
            }

        mGeoChatUiStateTestData = ChatUiStateTestData(
            adapter!!,
            geoChatStateFlowFieldReflection.get(mModel) as MutableStateFlow<ChatState?>,
            mModel.geoChatUiStateFlow
        )
    }

    @Test
    fun allElementsAreInPlaceTest() {
        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(
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
        val users = listOf(User(0, "User 1"))

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(Chat(), messages, users)
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
        Espresso.onView(withText("05:31 PM"))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun settingGeoChatUiStateWithThreeMessagesLeadsToThreeMessagesAppearInChatRecyclerViewTest() {
        val messages = listOf<Message>()
        val users = listOf<User>()

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(Chat(), messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(messages.size)))

        val endingMessages = listOf(
            Message(0, 0, "Hi!", 1696847478000),
            Message(1, 1, "qq", 1696847479000),
            Message(2, 0, "wassup?", 1696847480000),
        )
        val endingUsers = listOf(
            User(0, "User 1"),
            User(1, "User 2")
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(Chat(), endingMessages, endingUsers)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .perform(WaitingViewAction(500))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(endingMessages.size)))
    }

    @Test
    fun messagesScrolledDownOnLackingSpaceTest() {
        val messages = listOf<Message>()
        val users = listOf(
            User(0, "User 1"),
            User(1, "User 2")
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(Chat(), messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(messages.size)))

        val endingMessages = listOf(
            Message(0, 0, "Hi!", 1696847478000),
            Message(1, 1, "qq", 1696847479000),
            Message(2, 0, "wassup?", 1696847480000),
            Message(3, 1, "wassup?", 1696847481000),
            Message(4, 0, "wassup?", 1696847482000),
            Message(5, 1, "wassup?", 1696847483000),
            Message(6, 0, "wassup?", 1696847484000),
            Message(7, 1, "wassup?", 1696847485000),
            Message(8, 0, "wassup?", 1696847486000),
            Message(9, 1, "wassup?", 1696847487000),
            Message(10, 0, "wassup?", 1696847488000),
            Message(11, 1, "im leaving..", 1696847489000),
        )

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
            mGeoChatUiStateTestData.setChat(Chat(), messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(messages.size)))

        val endingUsers = listOf(
            User(0, "User 1"),
            User(1, "User 2")
        )
        val endingMessages = listOf(
            Message(0, 0, "Hi!", 1696847478000),
            Message(1, 1, "qq", 1696847479000),
            Message(2, 0, "wassup?", 1696847480000),
            Message(3, 1, "wassup?", 1696847481000),
            Message(4, 0, "wassup?", 1696847482000),
            Message(5, 1, "wassup?", 1696847483000),
            Message(6, 0, "wassup?", 1696847484000),
            Message(7, 1, "wassup?", 1696847485000),
            Message(8, 0, "wassup?", 1696847486000),
            Message(9, 1, "wassup?", 1696847487000),
            Message(10, 0, "wassup?", 1696847488000),
            Message(11, 1, "im leaving..", 1696847489000),
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(Chat(), endingMessages, endingUsers)
        }

        Espresso.onView(isRoot())
            .perform(WaitingViewAction(1000))
        Espresso.onView(withText(endingMessages.last().text))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun manualScrollingUpPreventsAutoScrollingTest() {
        val messages = listOf(
            Message(0, 0, "Hi!", 1696847478000),
            Message(1, 1, "qq", 1696847479000),
            Message(2, 0, "wassup?", 1696847480000),
            Message(3, 1, "wassup?", 1696847481000),
            Message(4, 0, "wassup?", 1696847482000),
            Message(5, 1, "wassup?", 1696847483000),
            Message(6, 0, "wassup?", 1696847484000),
            Message(7, 1, "wassup?", 1696847485000),
            Message(8, 0, "wassup?", 1696847486000),
            Message(9, 1, "wassup?", 1696847487000),
            Message(10, 0, "wassup?", 1696847488000),
            Message(11, 1, "im leaving..", 1696847489000),
        )
        val users = listOf(
            User(0, "User 1"),
            User(1, "User 2")
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(Chat(), messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .perform(ViewActions.swipeDown())

        val endingMessages = listOf(
            Message(12, 0, "Hi!", 1696847478000),
            Message(13, 1, "qq", 1696847479000),
        )

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

    @Test
    fun userInfoBottomSheetDisplayedOnMessageClickTest() {
        val messages = listOf(
            Message(0, 0, "Hi!", 1696847478000)
        )
        val users = listOf(
            User(0, "User 1")
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(Chat(), messages, users)
        }

        Espresso.onView(withText(users.first().username))
            .perform(SilentClickViewAction())
        Espresso.onView(ViewMatchers.isAssignableFrom(UserInfoBottomSheetContent::class.java))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun userInfoBottomSheetHasAllElementsVisibleAndEnabledTest() {
        val messages = listOf(
            Message(0, 0, "Hi!", 1696847478000)
        )
        val users = listOf(
            User(0, "User 1", "I'm fond of minecraft", false)
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(Chat(), messages, users)
        }

        Espresso.onView(withText(users.first().username))
            .perform(ViewActions.click())
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
        val messages = listOf(
            Message(0, 0, "Hi!", 1696847478000)
        )
        val users = listOf(
            User(0, "User 1", "I'm fond of minecraft", false)
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setChat(Chat(), messages, users)
        }

        Espresso.onView(withText(users.first().username))
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
        val error = LocalError(R.string.error_chat_message_sending_failed, ErrorBase.Level.NORMAL)

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.showError(error)
        }

        Espresso.onView(withText(error.messageResId))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun criticalErrorLeadsToClosingAppTest() {
        val error = LocalError(R.string.error_chat_message_sending_failed, ErrorBase.Level.NORMAL)

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