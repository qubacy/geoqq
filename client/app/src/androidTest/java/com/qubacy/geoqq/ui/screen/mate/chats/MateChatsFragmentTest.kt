package com.qubacy.geoqq.ui.screen.mate.chats

import android.view.View
import androidx.core.view.marginTop
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.card.MaterialCardView
import com.qubacy.geoqq.common.error.Error
import com.qubacy.geoqq.data.common.entity.chat.message.Message
import com.qubacy.geoqq.data.common.entity.person.user.User
import com.qubacy.geoqq.data.common.operation.HandleErrorOperation
import com.qubacy.geoqq.data.mates.chats.operation.UpdateChatOperation
import com.qubacy.geoqq.data.mates.chats.state.MateChatsState
import com.qubacy.geoqq.ui.screen.mate.chats.list.adapter.MateChatsAdapter
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.MateChatsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.data.mates.chats.entity.MateChatPreview
import com.qubacy.geoqq.data.mates.chats.operation.AddChatOperation
import com.qubacy.geoqq.data.mates.chats.operation.UpdateRequestCountOperation
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.ui.util.IsChildWithIndexViewAssertion
import com.qubacy.geoqq.ui.util.WaitingViewAction
import kotlinx.coroutines.launch
import org.hamcrest.Matchers
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MateChatsFragmentTest {
    class MateChatsUiStateTestData(
        private val mModel: MateChatsViewModel,
        private val mAdapter: MateChatsAdapter,
        private val mMateChatsStateFlow: MutableStateFlow<MateChatsState?>,
        private val mMateChatsUiState: LiveData<MateChatsUiState?>
    ) {
        fun setChats(chatPreviews: List<MateChatPreview>, users: List<User>) {
            val chatsState = MateChatsState(chatPreviews, users, 0, listOf())

            runBlocking {
                mMateChatsStateFlow.emit(chatsState)
            }

            mAdapter.setItems(chatPreviews)
        }

        fun addChat(chatPreview: MateChatPreview, users: List<User>) {
            val newChatPreviews = mutableListOf<MateChatPreview>()

            if (mMateChatsUiState.value != null)
                newChatPreviews.addAll(mMateChatsUiState.value!!.chatPreviews)

            newChatPreviews.add(chatPreview)

            val newUsers = mutableListOf<User>()

            if (mMateChatsUiState.value != null)
                newUsers.addAll(mMateChatsUiState.value!!.users)

            newUsers.addAll(users)

            val requestCount =
                if (mMateChatsUiState.value == null) 0 else mMateChatsUiState.value!!.requestCount

            val operations = listOf(
                AddChatOperation(chatPreview.chatId)
            )

            val chatsState = MateChatsState(newChatPreviews, newUsers, requestCount, operations)

            runBlocking {
                mMateChatsStateFlow.emit(chatsState)
            }
        }

        fun updateChat(chatPreview: MateChatPreview, users: List<User>) {
            val chatPreviews =
                if (mMateChatsUiState.value == null) listOf() else mMateChatsUiState.value!!.chatPreviews

            val modifiedChats: MutableList<MateChatPreview> = mutableListOf()

            modifiedChats.apply {
                addAll(chatPreviews)
            }

            val newUsers = mutableListOf<User>()

            if (mMateChatsUiState.value != null)
                newUsers.addAll(mMateChatsUiState.value!!.users)

            newUsers.addAll(users)

            var isUpdated = false

            for (chatPos in 0 until modifiedChats.size) {
                if (modifiedChats[chatPos].chatId == chatPreview.chatId) {
                    modifiedChats[chatPos] = chatPreview
                    isUpdated = true

                    break
                }
            }

            Assert.assertTrue(isUpdated)

            val requestCount =
                if (mMateChatsUiState.value == null) 0 else mMateChatsUiState.value!!.requestCount

            val operations = listOf(
                UpdateChatOperation(chatPreview.chatId)
            )

            val chatState = MateChatsState(modifiedChats, users, requestCount, operations)

            runBlocking {
                mMateChatsStateFlow.emit(chatState)
            }
        }

        fun setRequestCount(requestCount: Int) {
            val users =
                if (mMateChatsUiState.value == null) listOf() else mMateChatsUiState.value!!.users
            val chats =
                if (mMateChatsUiState.value == null) listOf() else mMateChatsUiState.value!!.chatPreviews

            val operations = listOf(
                UpdateRequestCountOperation()
            )

            val chatsState = MateChatsState(chats, users, requestCount, operations)

            runBlocking {
                mMateChatsStateFlow.emit(chatsState)
            }
        }

        fun showError(error: Error) {
            val users =
                if (mMateChatsUiState.value == null) listOf() else mMateChatsUiState.value!!.users
            val chats =
                if (mMateChatsUiState.value == null) listOf() else mMateChatsUiState.value!!.chatPreviews
            val requestCount =
                if (mMateChatsUiState.value == null) 0 else mMateChatsUiState.value!!.requestCount

            val operations = listOf(
                HandleErrorOperation(error)
            )

            val chatsState = MateChatsState(chats, users, requestCount, operations)

            runBlocking {
                mMateChatsStateFlow.emit(chatsState)
            }
        }
    }

    private lateinit var mMateChatsFragmentScenarioRule: FragmentScenario<MateChatsFragment>

    private lateinit var mModel: MateChatsViewModel
    private lateinit var mNavHostController: TestNavHostController
    private lateinit var mMateChatsUiStateTestData: MateChatsUiStateTestData

    @Before
    fun setup() {
        mMateChatsFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_Mates)
        mMateChatsFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        var fragment: MateChatsFragment? = null

        mNavHostController = TestNavHostController(ApplicationProvider.getApplicationContext())

        mMateChatsFragmentScenarioRule.onFragment {
            mNavHostController.setGraph(R.navigation.nav_graph)
            mNavHostController.setCurrentDestination(R.id.mateChatsFragment)
            Navigation.setViewNavController(it.requireView(), mNavHostController)

            mModel = ViewModelProvider(it)[MateChatsViewModel::class.java]
            fragment = it
        }

        val adapterFieldReflection = MateChatsFragment::class.java
            .getDeclaredField("mAdapter")
            .apply {
                isAccessible = true
            }
        val mateChatsStateFlowFieldReflection =
            MateChatsViewModel::class.java.getDeclaredField("mMateChatsStateFlow")
                .apply {
                    isAccessible = true
                }
        val mateChatsUiStateFieldReflection = MateChatsViewModel::class.java
            .getDeclaredField("mateChatsUiStateFlow")
            .apply {
                isAccessible = true
            }

        mMateChatsUiStateTestData = MateChatsUiStateTestData(
            mModel,
            adapterFieldReflection.get(fragment) as MateChatsAdapter,
            mateChatsStateFlowFieldReflection.get(mModel) as MutableStateFlow<MateChatsState?>,
            mateChatsUiStateFieldReflection.get(mModel) as LiveData<MateChatsUiState?>
        )
    }

    @Test
    fun allElementsInPlaceTest() {
        // it's hidden by default now:
//        Espresso.onView(withId(R.id.friend_requests_card_button))
//            .check(
//                ViewAssertions.matches(
//                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.chats_recycler_view))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun allElementsEnabled() {
        // it's hidden by default now:
//        Espresso.onView(withId(R.id.friend_requests_card_button))
//            .perform(ViewActions.click())
//            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.chats_recycler_view))
            .perform(ViewActions.swipeDown())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun addedChatHasCorrectChatNameLastMessageTextAndTimestampTest() {
        val chatPreview = MateChatPreview(2, null, "chat 1",
            Message(0, 0, "hi", 1696847478000))
        val users = listOf(
            User(0, "me")
        )

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.addChat(chatPreview, users)
        }

        Espresso.onView(withText(chatPreview.chatName))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(chatPreview.lastMessage.text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText("05:31 PM"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun settingThreeChatsLeadsToShowingThreeChatPreviewsTest() {
        val chatPreviews = listOf(
            MateChatPreview(0, null, "chat 1",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(0, null,  "chat 2",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(0, null,  "chat 3",
                Message(0, 0, "hi", 1696847478000)),
        )
        val users = listOf(
            User(0, "me")
        )

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setChats(chatPreviews, users)
        }

        Espresso.onView(withId(R.id.chats_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(chatPreviews.size)))
    }

    @Test
    fun newChatGoesAtTopTest() {
        val chatPreviews = listOf(
            MateChatPreview(1, null, "chat 2",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(0, null, "chat 1",
                Message(0, 0, "hi", 1696847478000)),
        )
        val users = listOf(
            User(0, "me")
        )

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setChats(chatPreviews, users)
        }

        val newChatPreview = MateChatPreview(2, null, "new chat",
            Message(0, 0, "hi", 1696847478000))

        mMateChatsFragmentScenarioRule.onFragment {
            it.lifecycleScope.launch {
                mMateChatsUiStateTestData.addChat(newChatPreview, listOf())
            }
        }

        Espresso.onView(
            Matchers.allOf(
            isDescendantOfA(withId(R.id.chats_recycler_view)),
            isAssignableFrom(MaterialCardView::class.java),
            hasDescendant(withText(newChatPreview.chatName))
        )).check(IsChildWithIndexViewAssertion(0))
    }

    @Test
    fun scrollingUpOnNewItemsAddingTest() {
        val users = listOf(
            User(0, "me")
        )
        val chatPreviews = listOf(
            MateChatPreview(11, null, "chat 12",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(10, null, "chat 11",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(9, null, "chat 10",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(8, null, "chat 9",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(7, null, "chat 8",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(6, null, "chat 7",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(5, null, "chat 6",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(4, null, "chat 5",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(3, null, "chat 4",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(2, null, "chat 3",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(1, null, "chat 2",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(0, null, "chat 1",
                Message(0, 0, "hi", 1696847478000)),
        )

        for (chatPreview in chatPreviews) {
            mMateChatsFragmentScenarioRule.onFragment {
                mMateChatsUiStateTestData.addChat(chatPreview, users)
            }

            Espresso.onView(withText(chatPreview.chatName))
                .perform(WaitingViewAction(500))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun chatPreviewGoesUpOnChatUpdateTest() {
        val chatPreviews = listOf(
            MateChatPreview(4, null, "chat 5",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(3, null, "chat 4",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(2, null, "chat 3",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(1, null, "chat 2",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(0, null, "chat 1",
                Message(0, 0, "hi", 1696847478000)),
        )
        val users = listOf(
            User(0, "me")
        )

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setChats(chatPreviews, users)
        }

        Espresso.onView(isRoot())
            .perform(WaitingViewAction(1000))

        val updatedChatPreview = MateChatPreview(0, null, "chat 1",
            Message(0, 0, "qqqqqq", 1696847480000))

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.updateChat(updatedChatPreview, listOf())
        }

        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.chats_recycler_view)),
            isAssignableFrom(MaterialCardView::class.java),
            hasDescendant(withText(updatedChatPreview.chatName))
        )).check(IsChildWithIndexViewAssertion(0))
    }

    @Test
    fun newRequestsCardAppearsOnRequestCountSetToOneTest() {
        Espresso.onView(withId(R.id.mate_requests_card))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setRequestCount(1)
        }

        Espresso.onView(withId(R.id.mate_requests_card))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    class TopBoundViewAssertion(private val mTop: Float) : ViewAssertion {
        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
            if (view == null)
                throw IllegalArgumentException()
            if (view.top.toFloat() != mTop)
                throw NoMatchingViewException.Builder().build()
        }
    }

    @Test
    fun newRequestsCardAppearanceChangesChatsListTopBoundTest() {
        val chatPreviews = listOf(
            MateChatPreview(4, null, "chat 5",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(3, null, "chat 4",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(2, null, "chat 3",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(1, null, "chat 2",
                Message(0, 0, "hi", 1696847478000)),
            MateChatPreview(0, null, "chat 1",
                Message(0, 0, "hi", 1696847478000)),
        )
        val users = listOf(
            User(0, "me")
        )

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setChats(chatPreviews, users)
        }

        Espresso.onView(isRoot())
            .perform(WaitingViewAction(1000))
            .check(TopBoundViewAssertion(0f))

        var binding: FragmentMateChatsBinding? = null

        mMateChatsFragmentScenarioRule.onFragment {
            binding = FragmentMateChatsBinding.bind(it.view!!)

            mMateChatsUiStateTestData.setRequestCount(1)
        }

        val topBound = binding!!.mateRequestsCard.measuredHeight.toFloat() +
                binding!!.mateRequestsCard.marginTop.toFloat()

        Espresso.onView(withId(R.id.chats_recycler_view))
            .perform(WaitingViewAction(1000))
            .check(TopBoundViewAssertion(topBound))
    }

    @Test
    fun mateRequestsCardDisappearsOnRequestCounterSettingToZeroTest() {
        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setRequestCount(1)
        }

        Espresso.onView(withId(R.id.mate_requests_card))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setRequestCount(0)
        }

        Espresso.onView(withId(R.id.mate_requests_card))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun handlingNormalErrorOperationLeadsToShowingDialogTest() {
        val error = Error(R.string.error_chat_message_sending_failed, Error.Level.NORMAL)

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.showError(error)
        }

        Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
            .perform(ViewActions.click())
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun handlingCriticalErrorOperationLeadsToAppClosingTest() {
        val error = Error(R.string.error_chat_message_sending_failed, Error.Level.CRITICAL)

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.showError(error)
        }

        try {
            Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
                .perform(ViewActions.click())

        } catch (e: Exception) {
            Assert.assertEquals(NoActivityResumedException::class, e::class)
        }
    }

    @Test
    fun chatPreviewClickLeadsToTransitionToMateChatFragmentTest() {
        val chatPreviews = listOf(
            MateChatPreview(0, null, "chat",
                Message(0, 0, "hi", 1696847478000))
        )
        val users = listOf(
            User(0, "me")
        )

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setChats(chatPreviews, users)
        }

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isAssignableFrom(MaterialCardView::class.java),
                ViewMatchers.isDescendantOfA(withId(R.id.chats_recycler_view)),
                ViewMatchers.hasDescendant(withText(chatPreviews[0].chatName))
            )
        ).perform(ViewActions.click())

        Assert.assertEquals(R.id.mateChatFragment, mNavHostController.currentDestination?.id)
    }

    @Test
    fun mateRequestsCheckButtonClickLeadsToTransitionToMateRequestsFragmentTest() {
        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setRequestCount(1)
        }

        Espresso.onView(withId(R.id.mate_requests_card_button))
            .perform(ViewActions.click())

        Assert.assertEquals(R.id.mateRequestsFragment, mNavHostController.currentDestination?.id)
    }
}