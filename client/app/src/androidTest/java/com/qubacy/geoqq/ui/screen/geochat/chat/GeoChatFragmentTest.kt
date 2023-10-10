package com.qubacy.geoqq.ui.screen.geochat.chat

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.data.common.entity.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatUiState
import com.qubacy.geoqq.ui.screen.geochat.chat.model.GeoChatViewModel
import com.qubacy.geoqq.ui.util.MaterialTextInputVisualLineCountViewAssertion
import com.qubacy.geoqq.ui.util.ScrollOccurredViewAssertion
import com.qubacy.geoqq.ui.util.WaitingViewAction
import org.junit.Test
import java.lang.reflect.Field


@RunWith(AndroidJUnit4::class)
class GeoChatFragmentTest {
    class GeoChatUiStateTestData(
        private val mModel: GeoChatViewModel,
        private val mGeoChatUiState: MutableLiveData<GeoChatUiState>
    ) {
        fun setGeoChatUiState(geoChatUiState: GeoChatUiState) {
            mGeoChatUiState.value = geoChatUiState
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

        val geoChatUiStateFieldReflection = GeoChatViewModel::class.java.getDeclaredField("mGeoChatUiState")
            .apply {
                isAccessible = true
            }

        mGeoChatUiStateTestData = GeoChatUiStateTestData(
            model!!,
            geoChatUiStateFieldReflection.get(model) as MutableLiveData<GeoChatUiState>
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
        val endingUiState = GeoChatUiState(
            listOf(
                User(0, "User 1")
            ),
            listOf(
                Message(0, "Hi!", 1696847478000)
            )
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setGeoChatUiState(endingUiState)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(endingUiState.messageList.size)))
        Espresso.onView(withText(endingUiState.userList[0].username))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withText(endingUiState.messageList[0].text))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withText("05:31 PM"))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun settingGeoChatUiStateWithThreeMessagesLeadsToThreeMessagesAppearInChatRecyclerViewTest() {
        val initUiState = GeoChatUiState(listOf(), listOf())

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setGeoChatUiState(initUiState)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(initUiState.messageList.size)))

        val endingUiState = GeoChatUiState(
            listOf(
                User(0, "User 1"),
                User(1, "User 2")
            ),
            listOf(
                Message(0, "Hi!", 1696847478000),
                Message(1, "qq", 1696847479000),
                Message(0, "wassup?", 1696847480000),
            )
        )

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setGeoChatUiState(endingUiState)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(endingUiState.messageList.size)))
    }

    @Test
    fun messagesScrolledDownOnLackingSpaceTest() {
        val initUiState = GeoChatUiState(listOf(), listOf())

        mGeoChatFragmentScenarioRule.onFragment {
            mGeoChatUiStateTestData.setGeoChatUiState(initUiState)
        }

        Espresso.onView(withId(R.id.chat_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(initUiState.messageList.size)))

        val users = listOf(
            User(0, "User 1"),
            User(1, "User 2")
        )
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

        val curMessages = mutableListOf<Message>()

        for (message in messages) {
            curMessages.add(message)

            val endingUiState = GeoChatUiState(
                users,
                curMessages
            )

            mGeoChatFragmentScenarioRule.onFragment {
                mGeoChatUiStateTestData.setGeoChatUiState(endingUiState)
            }

            Espresso.onView(withId(R.id.chat_recycler_view))
                .perform(WaitingViewAction(1000))
        }

        Espresso.onView(withText(messages.last().text))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }
}