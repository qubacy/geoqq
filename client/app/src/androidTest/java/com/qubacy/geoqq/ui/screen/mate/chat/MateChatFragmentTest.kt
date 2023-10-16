package com.qubacy.geoqq.ui.screen.mate.chat

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.data.common.chat.operation.AddMessageChatOperation
import com.qubacy.geoqq.data.common.chat.operation.AddUserChatOperation
import com.qubacy.geoqq.data.common.chat.state.ChatState
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapter
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.util.MaterialTextInputVisualLineCountViewAssertion
import com.qubacy.geoqq.ui.util.WaitingViewAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MateChatFragmentTest {

    class MateChatUiStateTestData(
        private val mModel: MateChatViewModel,
        private val mAdapter: ChatAdapter,
        private val mMateChatStateFlow: MutableStateFlow<ChatState?>,
        private val mMateUiState: LiveData<ChatUiState?>
    ) {
        fun setChat(messages: List<Message>, users: List<User>) {
            val chatState = ChatState(messages, users, listOf())

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }

            mAdapter.setItems(messages)
        }

        fun addMessage(message: Message) {
            val newMessages = mutableListOf<Message>()

            if (mMateUiState.value != null)
                newMessages.addAll(mMateUiState.value!!.messages)

            newMessages.add(message)

            val users = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.users

            val operations = listOf(
                AddMessageChatOperation(message.messageId)
            )

            val chatState = ChatState(newMessages, users, operations)

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }
        }

        fun addUser(user: User) {
            val newUsers = mutableListOf<User>()

            if (mMateUiState.value != null)
                newUsers.addAll(mMateUiState.value!!.users)

            newUsers.add(user)

            val messages = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.messages

            val operations = listOf(
                AddUserChatOperation(user.userId)
            )

            val chatState = ChatState(messages, newUsers, operations)

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }
        }

        fun showError(error: Error) {
            val users = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.users
            val messages = if (mMateUiState.value == null) listOf() else mMateUiState.value!!.messages

            val operations = listOf(
                HandleErrorOperation(error)
            )

            val chatState = ChatState(messages, users, operations)

            runBlocking {
                mMateChatStateFlow.emit(chatState)
            }
        }
    }

    private lateinit var mMateChatFragmentScenarioRule: FragmentScenario<MateChatFragment>
    private lateinit var mModel: MateChatViewModel

    private lateinit var mMateChatUiStateTestData: MateChatUiStateTestData

    @Before
    fun setup() {
        mMateChatFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_Mates)
        mMateChatFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        val adapterFieldReflection = MateChatFragment::class.java
            .getDeclaredField("mAdapter")
            .apply {
                isAccessible = true
            }

        var adapter: ChatAdapter? = null

        mMateChatFragmentScenarioRule.onFragment {
            mModel = ViewModelProvider(it)[MateChatViewModel::class.java]
            adapter = adapterFieldReflection.get(it) as ChatAdapter
        }

        val mateChatStateFlowFieldReflection = MateChatViewModel::class.java
            .getDeclaredField("mMateChatStateFlow")
            .apply {
                isAccessible = true
            }

        mMateChatUiStateTestData = MateChatUiStateTestData(
            mModel,
            adapter!!,
            mateChatStateFlowFieldReflection.get(mModel) as MutableStateFlow<ChatState?>,
            mModel.mateChatUiStateFlow
        )
    }

    @Test
    fun allElementsInPlaceTest() {
        Espresso.onView(ViewMatchers.withId(R.id.chat_recycler_view))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.sending_button))
            .check(
                ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun allElementsEnabledTest() {
        Espresso.onView(ViewMatchers.withId(R.id.chat_recycler_view))
            .perform(ViewActions.swipeDown())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(ViewMatchers.withId(R.id.sending_button))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun clickingSendButtonWithEmptyMessageFieldLeadsToErrorTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .perform(ViewActions.clearText())
        Espresso.onView(ViewMatchers.withId(R.id.sending_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.error_chat_message_incorrect))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun emptyMessageSnackbarDisappearsOnDismissButtonClickedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .perform(ViewActions.clearText())
        Espresso.onView(ViewMatchers.withId(R.id.sending_button))
            .perform(ViewActions.click())
        Espresso.onView(
            ViewMatchers.withText(R.string.fragment_base_show_message_action_dismiss_text))
            .perform(ViewActions.click())
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun clickingSendButtonWithNotEmptyMessageFieldCleansItAndGoesWithoutErrorTest() {
        val messageText = "Hi there"

        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .perform(ViewActions.typeText(messageText))
        Espresso.onView(ViewMatchers.withId(R.id.sending_button))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
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

        Espresso.onView(ViewMatchers.withId(R.id.sending_message))
            .perform(ViewActions.typeText(longText))
            .check(MaterialTextInputVisualLineCountViewAssertion(2))
    }

    @Test
    fun messageAppearsOnAddMessageOperationGottenOperationGottenTest() {
        val user = User(0, "user")
        val message = Message(0, 0, "message", 1697433645440)

        mMateChatFragmentScenarioRule.onFragment {
            mMateChatUiStateTestData.addUser(user)
            mMateChatUiStateTestData.addMessage(message)
        }

        Espresso.onView(withText(message.text))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun threeMessagesAppearOnSetChatOperationWithThreeMessagesGottenTest() {
        val users = listOf(
            User(0, "me"),
            User(1, "other"),
        )
        val messages = listOf(
            Message(0, 0, "hi", 1697441985606),
            Message(1, 1, "hi you", 1697441985606),
            Message(2, 0, "no hi you", 1697441985606),
        )

        mMateChatFragmentScenarioRule.onFragment {
            mMateChatUiStateTestData.setChat(messages, users)
        }

        for (message in messages) {
            Espresso.onView(withText(message.text))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun scrollingDownOnNewMessagesAddingTest() {
        val users = listOf(
            User(0, "me"),
            User(1, "other"),
        )
        val messages = listOf(
            Message(0, 0, "hi", 1697441985606),
            Message(1, 1, "hi you", 1697441985606),
            Message(2, 0, "no hi you", 1697441985606),
            Message(3, 0, "2", 1697441985606),
            Message(4, 0, "no hi you 2", 1697441985606),
            Message(5, 0, "no hi you 3", 1697441985606),
            Message(6, 0, "no hi you 4", 1697441985606),
            Message(7, 0, "no hi you 5", 1697441985606),
            Message(8, 0, "no hi you 6", 1697441985606),
            Message(9, 0, "no hi you 7", 1697441985606),
            Message(10, 1, "that's it", 1697441985606),
        )

        mMateChatFragmentScenarioRule.onFragment {
            mMateChatUiStateTestData.setChat(listOf(), users)
        }

        for (message in messages) {
            mMateChatUiStateTestData.addMessage(message)

            Espresso.onView(withText(message.text))
                .perform(WaitingViewAction(500))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun newUserProcessingTest() {
        val users = listOf(
            User(0, "me"),
            User(1, "other"),
        )
        val messages = listOf(
            Message(0, 0, "hi", 1697441985606),
            Message(1, 1, "hi you", 1697441985606),
            Message(2, 0, "no hi you", 1697441985606),
        )

        val newUser = User(2, "new")
        val newMessage = Message(3, 2, "hi there", 1697441985606)

        mMateChatFragmentScenarioRule.onFragment {
            mMateChatUiStateTestData.setChat(messages, users)
            mMateChatUiStateTestData.addUser(newUser)
            mMateChatUiStateTestData.addMessage(newMessage)
        }

        Espresso.onView(withText(newUser.username))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun handlingNormalErrorOperationLeadsToShowingDialogTest() {
        val error = Error(R.string.error_chat_message_sending_failed, Error.Level.NORMAL)

        mMateChatFragmentScenarioRule.onFragment {
            mMateChatUiStateTestData.showError(error)
        }

        Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
            .perform(ViewActions.click())
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun handlingCriticalErrorOperationLeadsToAppClosingTest() {
        val error = Error(R.string.error_chat_message_sending_failed, Error.Level.CRITICAL)

        mMateChatFragmentScenarioRule.onFragment {
            mMateChatUiStateTestData.showError(error)
        }

        try {
            Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
                .perform(ViewActions.click())

        } catch (e: Exception) {
            Assert.assertEquals(NoActivityResumedException::class, e::class)
        }
    }
}