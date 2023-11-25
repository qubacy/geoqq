package com.qubacy.geoqq.ui.screen.mate.chat

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
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
import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.common.operation.common.Operation
import com.qubacy.geoqq.domain.mate.chat.state.MateChatState
import com.qubacy.geoqq.ui.common.fragment.common.base.BaseFragment
import com.qubacy.geoqq.ui.screen.common.ScreenContext
import com.qubacy.geoqq.ui.screen.common.fragment.chat.ChatFragmentContext
import com.qubacy.geoqq.ui.screen.common.fragment.chat.ChatFragmentTest
import com.qubacy.geoqq.ui.screen.common.chat.component.list.adapter.ChatAdapter
import com.qubacy.geoqq.ui.screen.common.chat.model.state.ChatUiState
import com.qubacy.geoqq.ui.screen.mate.chat.model.MateChatViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.MateChatsFragmentDirections
import com.qubacy.geoqq.ui.util.MaterialTextInputVisualLineCountViewAssertion
import com.qubacy.geoqq.ui.util.WaitingViewAction
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MateChatFragmentTest : ChatFragmentTest<MateChatState>() {
    class MateChatUiStateTestData(
        chatStateFlow: MutableStateFlow<MateChatState?>,
        chatUiState: LiveData<ChatUiState?>
    ) : ChatUiStateTestData<MateChatState>(
        chatStateFlow, chatUiState
    ) {
        override fun generateChatState(
            messages: List<Message>,
            users: List<User>,
            operations: List<Operation>
        ): MateChatState {
            return MateChatState(messages, users, operations)
        }
    }

    private lateinit var mMateChatFragmentScenarioRule: FragmentScenario<MateChatFragment>
    private lateinit var mModel: MateChatViewModel

    private lateinit var mMateChatUiStateTestData: MateChatUiStateTestData

    @Before
    override fun setup() {
        super.setup()

        val args = MateChatsFragmentDirections
            .actionMateChatsFragmentToMateChatFragment(0)
            .arguments

        mMateChatFragmentScenarioRule = launchFragmentInContainer(
            fragmentArgs = args,
            themeResId = R.style.Theme_Geoqq_Mates)
        mMateChatFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        val adapterFieldReflection = MateChatFragment::class.java
            .getDeclaredField("mAdapter")
            .apply {
                isAccessible = true
            }

        var fragment: MateChatFragment? = null

        mMateChatFragmentScenarioRule.moveToState(Lifecycle.State.STARTED)
        mMateChatFragmentScenarioRule.onFragment {
            fragment = it
        }

        val modelFieldReflection = BaseFragment::class.java.getDeclaredField("mModel")
            .apply { isAccessible = true }
        val mateChatStateFlowFieldReflection = MateChatViewModel::class.java
            .getDeclaredField("mMateChatStateFlow")
            .apply { isAccessible = true }

        mModel = modelFieldReflection.get(fragment) as MateChatViewModel
        mMateChatUiStateTestData = MateChatUiStateTestData(
            mateChatStateFlowFieldReflection.get(mModel) as MutableStateFlow<MateChatState?>,
            mModel.mateChatUiStateFlow as LiveData<ChatUiState?>
        )

        mMateChatUiStateTestData.setChat(listOf(), TEST_USERS)
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
        val users = ScreenContext.generateTestUsers(2, true)

        mMateChatFragmentScenarioRule.onFragment {
            mMateChatUiStateTestData.setChat(listOf(), users)
        }

        val newMessage = Message(0, 0, "message", 1697433645440)

        mMateChatFragmentScenarioRule.onFragment {
            mMateChatUiStateTestData.addMessage(newMessage)
        }

        Espresso.onView(withText(newMessage.text))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun threeMessagesAppearOnSetChatOperationWithThreeMessagesGottenTest() {
        val users = ScreenContext.generateTestUsers(2, true)
        val messages = ChatFragmentContext.generateTestMessages(3)

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
        val users = ScreenContext.generateTestUsers(2, true)
        val messages = ChatFragmentContext.generateTestMessages(11)

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
    fun handlingNormalErrorOperationLeadsToShowingDialogTest() {
        val error = Error(0, "Test", false)

        mMateChatFragmentScenarioRule.onFragment {
            mMateChatUiStateTestData.showError(error)
        }

        Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
            .perform(ViewActions.click())
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun handlingCriticalErrorOperationLeadsToAppClosingTest() {
        val error = Error(0, "Test", true)

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