package com.qubacy.geoqq.ui.screen.mate.chats

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.card.MaterialCardView
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.data.common.entity.chat.Chat
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.ui.common.fragment.common.model.operation.common.UiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.operation.AddChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.operation.ModifyChatUiOperation
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.operation.SetChatsUiOperation
import com.qubacy.geoqq.ui.util.IsChildWithIndexViewAssertion
import com.qubacy.geoqq.ui.util.WaitingViewAction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.hamcrest.Matchers
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MateChatsFragmentTest {
    class MateChatsUiStateTestData(
        private val mModel: MateChatsViewModel,
        private val mMateChatsUiOperationFlow: MutableStateFlow<UiOperation?>
    ) {
        suspend fun setChats(chats: List<Chat>) {
            mMateChatsUiOperationFlow.emit(SetChatsUiOperation(chats))
        }

        suspend fun addChat(chat: Chat) {
            mMateChatsUiOperationFlow.emit(AddChatUiOperation(chat))
        }

        suspend fun updateChat(chat: Chat) {
            mMateChatsUiOperationFlow.emit(ModifyChatUiOperation(chat))
        }
    }

    private lateinit var mMateChatsFragmentScenarioRule: FragmentScenario<MateChatsFragment>

    private lateinit var mModel: MateChatsViewModel
    private lateinit var mMateChatsUiStateTestData: MateChatsUiStateTestData

    @Before
    fun setup() {
        mMateChatsFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_Mates)
        mMateChatsFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        mMateChatsFragmentScenarioRule.onFragment {
            mModel = ViewModelProvider(it)[MateChatsViewModel::class.java]
        }

        val mateChatsUiOperationFlowFieldReflection =
            MateChatsViewModel::class.java.getDeclaredField("mMateChatsUiOperationFlow")
                .apply {
                    isAccessible = true
                }

        mMateChatsUiStateTestData = MateChatsUiStateTestData(
            mModel,
            mateChatsUiOperationFlowFieldReflection.get(mModel)  as MutableStateFlow<UiOperation?>
        )
    }

    @Test
    fun allElementsInPlaceTest() {
        Espresso.onView(withId(R.id.friend_requests_card_button))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.mates_recycler_view))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun allElementsEnabled() {
        Espresso.onView(withId(R.id.friend_requests_card_button))
            .perform(ViewActions.click())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.mates_recycler_view))
            .perform(ViewActions.swipeDown())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun addedChatHasCorrectChatNameLastMessageTextAndTimestampTest() {
        val chat = Chat(0, null, "chat 1",
            Message(0, "hi", 1696847478000))

        mMateChatsFragmentScenarioRule.onFragment {
            it.lifecycleScope.launch {
                mMateChatsUiStateTestData.addChat(chat)
            }
        }

        Espresso.onView(withText(chat.chatName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(chat.lastMessage.text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText("05:31 PM"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun settingThreeChatsLeadsToShowingThreeChatPreviewsTest() {
        val chats = listOf(
            Chat(0, null, "chat 1",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 2",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 3",
                Message(0, "hi", 1696847478000)),
        )

        mMateChatsFragmentScenarioRule.onFragment {
            it.lifecycleScope.launch {
                mMateChatsUiStateTestData.setChats(chats)
            }
        }

        Espresso.onView(withId(R.id.mates_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(chats.size)))
    }

    @Test
    fun newChatGoesAtTopTest() {
        val chats = listOf(
            Chat(0, null, "chat 2",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 1",
                Message(0, "hi", 1696847478000)),
        )

        mMateChatsFragmentScenarioRule.onFragment {
            it.lifecycleScope.launch {
                mMateChatsUiStateTestData.setChats(chats)
            }
        }

        val newChat = Chat(0, null, "new chat",
            Message(0, "hi", 1696847478000))

        mMateChatsFragmentScenarioRule.onFragment {
            it.lifecycleScope.launch {
                mMateChatsUiStateTestData.addChat(newChat)
            }
        }

        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.mates_recycler_view)),
            isAssignableFrom(MaterialCardView::class.java),
            hasDescendant(withText(newChat.chatName))
        )).check(IsChildWithIndexViewAssertion(0))
    }

    @Test
    fun scrollingUpOnNewItemsAddingTest() {
        val chats = listOf(
            Chat(0, null, "chat 12",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 11",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 10",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 9",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 8",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 7",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 6",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 5",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 4",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 3",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 2",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 1",
                Message(0, "hi", 1696847478000)),
        )

        for (chat in chats) {
            mMateChatsFragmentScenarioRule.onFragment {
                it.lifecycleScope.launch {
                    mMateChatsUiStateTestData.addChat(chat)
                }
            }

            Espresso.onView(withText(chat.chatName))
                .perform(WaitingViewAction(500))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun chatPreviewGoesUpOnChatUpdateTest() {
        val chats = listOf(
            Chat(4, null, "chat 5",
                Message(0, "hi", 1696847478000)),
            Chat(3, null, "chat 4",
                Message(0, "hi", 1696847478000)),
            Chat(2, null, "chat 3",
                Message(0, "hi", 1696847478000)),
            Chat(1, null, "chat 2",
                Message(0, "hi", 1696847478000)),
            Chat(0, null, "chat 1",
                Message(0, "hi", 1696847478000)),
        )

        mMateChatsFragmentScenarioRule.onFragment {
            it.lifecycleScope.launch {
                mMateChatsUiStateTestData.setChats(chats)
            }
        }

        val updatedChat = Chat(0, null, "chat 1",
            Message(0, "qqqqqq", 1696847480000))

        mMateChatsFragmentScenarioRule.onFragment {
            it.lifecycleScope.launch {
                mMateChatsUiStateTestData.updateChat(updatedChat)
            }
        }

        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.mates_recycler_view)),
            isAssignableFrom(MaterialCardView::class.java),
            hasDescendant(withText(updatedChat.chatName))
        )).check(IsChildWithIndexViewAssertion(0))
    }
}