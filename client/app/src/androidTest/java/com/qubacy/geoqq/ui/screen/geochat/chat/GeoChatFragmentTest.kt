package com.qubacy.geoqq.ui.screen.geochat.chat

import android.util.Log
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qubacy.geoqq.R
import com.qubacy.geoqq.data.common.entity.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.ui.common.component.bottomsheet.userinfo.UserInfoBottomSheetContent
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.screen.common.chat.model.state.operation.AddMessageUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.state.operation.ChatUiOperation
import com.qubacy.geoqq.ui.screen.common.chat.model.state.operation.SetMessagesUiOperation
import com.qubacy.geoqq.ui.util.DragBottomSheetViewAction
import com.qubacy.geoqq.ui.util.MaterialTextInputVisualLineCountViewAssertion
import com.qubacy.geoqq.ui.util.WaitingViewAction
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GeoChatFragmentTest {
    companion object {
        const val TAG = "GeoChatTest"
    }

    class GeoChatUiStateTestData(
        private val mModel: GeoChatViewModel,
        private val mGeoChatUiOperation: MutableLiveData<ChatUiOperation>,
        private var mGeoChatUiState: MutableLiveData<ChatUiState>
    ) {
        fun addMessage(message: Message, user: User) {
            mGeoChatUiState.value!!.users.add(user)
            mGeoChatUiState.value!!.messages.add(message)

            Log.d(TAG, "addMessage(): newMessage = ${message.text} from = ${message.userId}")

            mGeoChatUiOperation.value = AddMessageUiOperation(message)
        }

        fun setMessages(messages: List<Message>, users: List<User>) {
            mGeoChatUiOperation.value = SetMessagesUiOperation(messages)

            val mutableMessages = mutableListOf<Message>()
            val mutableUsers = mutableListOf<User>()

            for (message in messages) mutableMessages.add(message)
            for (user in users) mutableUsers.add(user)

            mGeoChatUiState.value = ChatUiState(
                mutableMessages,
                mutableUsers
            )
        }

    }

    private lateinit var mGeoChatFragmentScenarioRule: FragmentScenario<GeoChatFragment>
    private lateinit var mGeoChatUiStateTestData: GeoChatUiStateTestData

    @Before
    fun setup() {
        mGeoChatFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_GeoChat)

        var model: GeoChatViewModel? = null

        mGeoChatFragmentScenarioRule.apply {
            moveToState(Lifecycle.State.RESUMED)
            onFragment {
                model = ViewModelProvider(it)[GeoChatViewModel::class.java]
            }
        }

        val geoChatUiOperationFieldReflection = GeoChatViewModel::class.java.getDeclaredField("mGeoChatUiOperation")
            .apply {
                isAccessible = true
            }
        val geoChatUiStateFieldReflection = GeoChatViewModel::class.java.getDeclaredField("mGeoChatUiState")
            .apply {
                isAccessible = true
            }


        mGeoChatUiStateTestData = GeoChatUiStateTestData(
            model!!,
            geoChatUiOperationFieldReflection.get(model) as MutableLiveData<ChatUiOperation>,
            geoChatUiStateFieldReflection.get(model) as MutableLiveData<ChatUiState>
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
        val messages = listOf(Message(0, "Hi", 1696847478000))
        val users = listOf(User(0, "User 1"))

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setMessages(
                messages, users
            )
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
            mGeoChatUiStateTestData.setMessages(messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(messages.size)))

        val endingMessages = listOf(
            Message(0, "Hi!", 1696847478000),
            Message(1, "qq", 1696847479000),
            Message(0, "wassup?", 1696847480000),
        )
        val endingUsers = listOf(
            User(0, "User 1"),
            User(1, "User 2")
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setMessages(endingMessages, endingUsers)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(endingMessages.size)))
    }

    @Test
    fun messagesScrolledDownOnLackingSpaceTest() {
        val messages = listOf<Message>()
        val users = listOf<User>()

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setMessages(messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(messages.size)))

        val endingUsers = listOf(
            User(0, "User 1"),
            User(1, "User 2")
        )
        val endingMessages = listOf(
            Message(0, "Hi!", 1696847478000),
            Message(1, "qq", 1696847479000),
            Message(0, "wassup?", 1696847480000),
            Message(1, "wassup?", 1696847481000),
            Message(0, "wassup?", 1696847482000),
            Message(1, "wassup?", 1696847483000),
            Message(0, "wassup?", 1696847484000),
            Message(1, "wassup?", 1696847485000),
            Message(0, "wassup?", 1696847486000),
            Message(1, "wassup?", 1696847487000),
            Message(0, "wassup?", 1696847488000),
            Message(1, "im leaving..", 1696847489000),
        )

        for (endingMessage in endingMessages) {
            mGeoChatFragmentScenarioRule.onFragment {
                mGeoChatUiStateTestData.addMessage(
                    endingMessage,
                    endingUsers.find { user -> user.userId == endingMessage.userId }!!
                )
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
            mGeoChatUiStateTestData.setMessages(messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(messages.size)))

        val endingUsers = listOf(
            User(0, "User 1"),
            User(1, "User 2")
        )
        val endingMessages = listOf(
            Message(0, "Hi!", 1696847478000),
            Message(1, "qq", 1696847479000),
            Message(0, "wassup?", 1696847480000),
            Message(1, "wassup?", 1696847481000),
            Message(0, "wassup?", 1696847482000),
            Message(1, "wassup?", 1696847483000),
            Message(0, "wassup?", 1696847484000),
            Message(1, "wassup?", 1696847485000),
            Message(0, "wassup?", 1696847486000),
            Message(1, "wassup?", 1696847487000),
            Message(0, "wassup?", 1696847488000),
            Message(1, "im leaving..", 1696847489000),
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setMessages(endingMessages, endingUsers)
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
            Message(0, "Hi!", 1696847478000),
            Message(1, "qq", 1696847479000),
            Message(0, "wassup?", 1696847480000),
            Message(1, "wassup?", 1696847481000),
            Message(0, "wassup?", 1696847482000),
            Message(1, "wassup?", 1696847483000),
            Message(0, "wassup?", 1696847484000),
            Message(1, "wassup?", 1696847485000),
            Message(0, "wassup?", 1696847486000),
            Message(1, "wassup?", 1696847487000),
            Message(0, "wassup?", 1696847488000),
            Message(1, "im leaving..", 1696847489000),
        )
        val users = listOf(
            User(0, "User 1"),
            User(1, "User 2")
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setMessages(messages, users)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .perform(ViewActions.swipeDown())

        val endingMessages = listOf(
            Message(0, "Hi!", 1696847478000),
            Message(1, "qq", 1696847479000),
        )

        for (endingMessage in endingMessages) {
            mGeoChatFragmentScenarioRule.onFragment {
                mGeoChatUiStateTestData.addMessage(
                    endingMessage,
                    users.find { user -> user.userId == endingMessage.userId }!!
                )
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
            Message(0, "Hi!", 1696847478000)
        )
        val users = listOf(
            User(0, "User 1")
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setMessages(messages, users)
        }

        Espresso.onView(withText(users.first().username))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isAssignableFrom(UserInfoBottomSheetContent::class.java))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun userInfoBottomSheetHasAllElementsVisibleAndEnabledTest() {
        val messages = listOf(
            Message(0, "Hi!", 1696847478000)
        )
        val users = listOf(
            User(0, "User 1", "I'm fond of minecraft", false)
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setMessages(messages, users)
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
            Message(0, "Hi!", 1696847478000)
        )
        val users = listOf(
            User(0, "User 1", "I'm fond of minecraft", false)
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setMessages(messages, users)
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
}